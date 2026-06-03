package com.querypulse.backend.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.querypulse.backend.dto.CreateDatabaseRequest;
import com.querypulse.backend.entity.MonitoredDatabase;
import com.querypulse.backend.repository.MonitoredDatabaseRepository;
import com.querypulse.backend.service.DatabaseService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DatabaseServiceImpl
implements DatabaseService {

    private final MonitoredDatabaseRepository
            monitoredDatabaseRepository;

    @Override
    public MonitoredDatabase createDatabase(
            CreateDatabaseRequest request
    ) {

        MonitoredDatabase database =
                MonitoredDatabase.builder()

                .displayName(
                        request.getDisplayName()
                )

                .databaseType(
                        request.getDatabaseType()
                )

                .host(
                        request.getHost()
                )

                .port(
                        request.getPort()
                )

                .databaseName(
                        request.getDatabaseName()
                )

                .username(
                        request.getUsername()
                )

                .password(
                        request.getPassword()
                )

                .monitoringEnabled(true)

                .createdAt(
                        LocalDateTime.now()
                )

                .build();

        return monitoredDatabaseRepository
                .save(database);
    }

    @Override
    public List<MonitoredDatabase>
    getAllDatabases() {

        return monitoredDatabaseRepository
                .findAll();
    }
}