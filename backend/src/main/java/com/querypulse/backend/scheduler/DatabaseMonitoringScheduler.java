package com.querypulse.backend.scheduler;

import com.querypulse.backend.entity.DatabaseAlert;
import com.querypulse.backend.entity.MonitoredDatabase;
import com.querypulse.backend.entity.User;
import com.querypulse.backend.repository.DatabaseAlertRepository;
import com.querypulse.backend.repository.MonitoredDatabaseRepository;
import com.querypulse.backend.repository.UserRepository;
import com.querypulse.backend.service.DatabaseService;
import com.querypulse.backend.service.EmailService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class DatabaseMonitoringScheduler {

    private static final int CONNECTION_THRESHOLD = 50;
    private static final int TABLE_COUNT_THRESHOLD = 1000;

    private final MonitoredDatabaseRepository
            monitoredDatabaseRepository;

    private final DatabaseService
            databaseService;

    private final DatabaseAlertRepository
            databaseAlertRepository;

    private final UserRepository
            userRepository;

    private final EmailService
            emailService;

    @Scheduled(fixedRate = 300000)
    public void monitorDatabases() {

        log.info("Running automatic database monitoring...");

        List<MonitoredDatabase> databases =
                monitoredDatabaseRepository.findAll();

        for (MonitoredDatabase database : databases) {

            try {

                // Refresh live metrics + status (never throws — sets FAILED
                // on the entity if the database is unreachable) and persist a
                // history data point.
                databaseService.refreshDatabaseMetrics(database.getId());
                databaseService.saveHealthHistory(database.getId());

                MonitoredDatabase db =
                        monitoredDatabaseRepository
                                .findById(database.getId())
                                .orElseThrow();

                boolean isDown =
                        "FAILED".equalsIgnoreCase(db.getConnectionStatus());

                // DATABASE DOWN
                evaluateAlert(
                        db,
                        "DATABASE_DOWN",
                        "HIGH",
                        isDown,
                        "Database " + db.getDisplayName() + " is DOWN"
                );

                // HIGH CONNECTIONS (only meaningful while reachable)
                boolean highConnections =
                        !isDown
                                && db.getActiveConnections() != null
                                && db.getActiveConnections() > CONNECTION_THRESHOLD;
                evaluateAlert(
                        db,
                        "HIGH_CONNECTIONS",
                        "MEDIUM",
                        highConnections,
                        "Active connections exceeded threshold. Current connections = "
                                + db.getActiveConnections()
                );

                // TOO MANY TABLES (only meaningful while reachable)
                boolean highTableCount =
                        !isDown
                                && db.getTableCount() != null
                                && db.getTableCount() > TABLE_COUNT_THRESHOLD;
                evaluateAlert(
                        db,
                        "HIGH_TABLE_COUNT",
                        "LOW",
                        highTableCount,
                        "Table count exceeded threshold. Current count = "
                                + db.getTableCount()
                );

            } catch (Exception ex) {

                log.error("Monitoring failed for {} : {}",
                        database.getDisplayName(), ex.getMessage(), ex);
            }
        }
    }

    /**
     * Opens a new alert (and notifies users) the first time a condition is
     * met, and resolves the open alert once the condition clears. This makes
     * alerts idempotent across monitoring cycles — no duplicate rows and no
     * repeated email spam while a problem persists.
     */
    private void evaluateAlert(
            MonitoredDatabase database,
            String alertType,
            String severity,
            boolean conditionMet,
            String message
    ) {

        Optional<DatabaseAlert> openAlert =
                databaseAlertRepository
                        .findFirstByDatabaseIdAndAlertTypeAndResolvedFalse(
                                database.getId(), alertType);

        if (conditionMet) {

            if (openAlert.isEmpty()) {

                DatabaseAlert alert = DatabaseAlert.builder()
                        .databaseId(database.getId())
                        .alertType(alertType)
                        .severity(severity)
                        .message(message)
                        .createdAt(LocalDateTime.now())
                        .resolved(false)
                        .build();

                databaseAlertRepository.save(alert);
                notifyUsers(alert, database.getDisplayName());
            }
            // else: alert already open — do nothing (dedup, no re-email).

        } else if (openAlert.isPresent()) {

            DatabaseAlert alert = openAlert.get();
            alert.setResolved(true);
            alert.setResolvedAt(LocalDateTime.now());
            databaseAlertRepository.save(alert);
            log.info("Resolved {} alert for {}", alertType, database.getDisplayName());
            notifyRecovery(alert, database.getDisplayName());
        }
    }

    private void notifyUsers(DatabaseAlert alert, String databaseName) {

        for (User user : activeRecipients()) {
            emailService.sendAlertEmail(
                    user.getEmail(),
                    alert.getAlertType(),
                    databaseName,
                    alert.getSeverity(),
                    alert.getMessage()
            );
        }
    }

    private void notifyRecovery(DatabaseAlert alert, String databaseName) {

        for (User user : activeRecipients()) {
            emailService.sendRecoveryEmail(
                    user.getEmail(),
                    alert.getAlertType(),
                    databaseName
            );
        }
    }

    private List<User> activeRecipients() {
        return userRepository.findAll().stream()
                .filter(u -> u.getEmail() != null && Boolean.TRUE.equals(u.getIsActive()))
                .toList();
    }

}