package com.querypulse.backend.scheduler;

import com.querypulse.backend.entity.MonitoredDatabase;
import com.querypulse.backend.repository.MonitoredDatabaseRepository;
import com.querypulse.backend.service.DatabaseService;

import lombok.RequiredArgsConstructor;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class DatabaseMonitoringScheduler {

    private final MonitoredDatabaseRepository monitoredDatabaseRepository;

    private final DatabaseService databaseService;

    @Scheduled(fixedRate = 300000)
    public void monitorDatabases() {

        System.out.println(
                "Running automatic database monitoring..."
        );

        List<MonitoredDatabase> databases =
                monitoredDatabaseRepository.findAll();

        for (MonitoredDatabase database : databases) {

            try {

                databaseService.refreshDatabaseMetrics(
        database.getId()
);

databaseService.saveHealthHistory(
        database.getId()
);

            } catch (Exception ex) {

                System.out.println(
                        "Monitoring failed for "
                                + database.getDisplayName()
                );
            }
        }
    }

}