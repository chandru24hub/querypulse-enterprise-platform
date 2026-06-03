package com.querypulse.backend.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.querypulse.backend.dto.CreateDatabaseRequest;
import com.querypulse.backend.dto.DatabaseResponse;
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

    private final BCryptPasswordEncoder
            passwordEncoder =
            new BCryptPasswordEncoder();

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
                                passwordEncoder.encode(
                                        request.getPassword()
                                )
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
    public List<DatabaseResponse>
    getAllDatabases() {

        return monitoredDatabaseRepository
                .findAll()
                .stream()
                .map(database ->

                        DatabaseResponse.builder()

                                .id(
                                        database.getId()
                                )

                                .displayName(
                                        database.getDisplayName()
                                )

                                .databaseType(
                                        database.getDatabaseType()
                                )

                                .host(
                                        database.getHost()
                                )

                                .port(
                                        database.getPort()
                                )

                                .databaseName(
                                        database.getDatabaseName()
                                )

                                .username(
                                        database.getUsername()
                                )

                                .monitoringEnabled(
                                        database.getMonitoringEnabled()
                                )

                                .createdAt(
                                        database.getCreatedAt()
                                )

                                .build()
                )
                .collect(
                        Collectors.toList()
                );
    }
}