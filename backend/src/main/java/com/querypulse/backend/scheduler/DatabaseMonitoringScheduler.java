package com.querypulse.backend.scheduler;

import com.querypulse.backend.entity.DatabaseAlert;
import com.querypulse.backend.entity.MonitoredDatabase;
import com.querypulse.backend.repository.DatabaseAlertRepository;
import com.querypulse.backend.repository.MonitoredDatabaseRepository;
import com.querypulse.backend.service.DatabaseService;

import lombok.RequiredArgsConstructor;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DatabaseMonitoringScheduler {

    private final MonitoredDatabaseRepository
            monitoredDatabaseRepository;

    private final DatabaseService
            databaseService;

    private final DatabaseAlertRepository
            databaseAlertRepository;

    @Scheduled(fixedRate = 300000)
    public void monitorDatabases() {

        System.out.println(
                "Running automatic database monitoring..."
        );

        List<MonitoredDatabase> databases =
                monitoredDatabaseRepository.findAll();

        for (MonitoredDatabase database : databases) {

            try {

                //----------------------------------------
                // Refresh Metrics
                //----------------------------------------

                databaseService.refreshDatabaseMetrics(
                        database.getId()
                );

                //----------------------------------------
                // Save History
                //----------------------------------------

                databaseService.saveHealthHistory(
                        database.getId()
                );

                //----------------------------------------
                // Reload latest database state
                //----------------------------------------

                MonitoredDatabase updatedDatabase =
                        monitoredDatabaseRepository
                                .findById(
                                        database.getId()
                                )
                                .orElseThrow();

                //----------------------------------------
                // DATABASE DOWN ALERT
                //----------------------------------------

                if (
                        "FAILED".equalsIgnoreCase(
                                updatedDatabase.getConnectionStatus()
                        )
                ) {

                    DatabaseAlert alert =
                            DatabaseAlert
                                    .builder()

                                    .databaseId(
                                            updatedDatabase.getId()
                                    )

                                    .alertType(
                                            "DATABASE_DOWN"
                                    )

                                    .severity(
                                            "HIGH"
                                    )

                                    .message(
                                            "Database "
                                                    +
                                                    updatedDatabase.getDisplayName()
                                                    +
                                                    " is DOWN"
                                    )

                                    .createdAt(
                                            LocalDateTime.now()
                                    )

                                    .build();

                    databaseAlertRepository.save(
                            alert
                    );

                }

                //----------------------------------------
                // HIGH CONNECTION ALERT
                //----------------------------------------

                if (
                        updatedDatabase.getActiveConnections() != null
                                &&
                                updatedDatabase.getActiveConnections() > 50
                ) {

                    DatabaseAlert alert =
                            DatabaseAlert
                                    .builder()

                                    .databaseId(
                                            updatedDatabase.getId()
                                    )

                                    .alertType(
                                            "HIGH_CONNECTIONS"
                                    )

                                    .severity(
                                            "MEDIUM"
                                    )

                                    .message(
                                            "Active connections exceeded threshold. Current connections = "
                                                    +
                                                    updatedDatabase.getActiveConnections()
                                    )

                                    .createdAt(
                                            LocalDateTime.now()
                                    )

                                    .build();

                    databaseAlertRepository.save(
                            alert
                    );

                }

                //----------------------------------------
                // TOO MANY TABLES ALERT
                //----------------------------------------

                if (
                        updatedDatabase.getTableCount() != null
                                &&
                                updatedDatabase.getTableCount() > 1000
                ) {

                    DatabaseAlert alert =
                            DatabaseAlert
                                    .builder()

                                    .databaseId(
                                            updatedDatabase.getId()
                                    )

                                    .alertType(
                                            "HIGH_TABLE_COUNT"
                                    )

                                    .severity(
                                            "LOW"
                                    )

                                    .message(
                                            "Table count exceeded threshold. Current count = "
                                                    +
                                                    updatedDatabase.getTableCount()
                                    )

                                    .createdAt(
                                            LocalDateTime.now()
                                    )

                                    .build();

                    databaseAlertRepository.save(
                            alert
                    );

                }

            } catch (Exception ex) {

    System.out.println(

            "Monitoring failed for "

                    +

                    database.getDisplayName()

                    +

                    " : "

                    +

                    ex.getMessage()

    );

    DatabaseAlert alert =
            DatabaseAlert
                    .builder()

                    .databaseId(
                            database.getId()
                    )

                    .alertType(
                            "DATABASE_DOWN"
                    )

                    .severity(
                            "HIGH"
                    )

                    .message(
                            "Database "
                                    +
                                    database.getDisplayName()
                                    +
                                    " is DOWN"
                    )

                    .createdAt(
                            LocalDateTime.now()
                    )

                    .build();

    databaseAlertRepository.save(
            alert
    );

}

        }

    }

}