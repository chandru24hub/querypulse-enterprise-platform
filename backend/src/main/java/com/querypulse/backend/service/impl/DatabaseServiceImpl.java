package com.querypulse.backend.service.impl;

import java.sql.Connection;
import java.sql.DriverManager;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import com.querypulse.backend.security.AesEncryptionService;
import org.springframework.stereotype.Service;

import com.querypulse.backend.dto.CreateDatabaseRequest;
import com.querypulse.backend.dto.DatabaseResponse;
import com.querypulse.backend.dto.ConnectionTestResponse;
import com.querypulse.backend.entity.MonitoredDatabase;
import com.querypulse.backend.enums.DatabaseType;
import com.querypulse.backend.repository.MonitoredDatabaseRepository;
import com.querypulse.backend.service.DatabaseService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DatabaseServiceImpl
implements DatabaseService {

    private final MonitoredDatabaseRepository
            monitoredDatabaseRepository;

    private final AesEncryptionService
            aesEncryptionService;

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
                                aesEncryptionService.encrypt(
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

                                .id(database.getId())

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

                                .connectionStatus(
        database.getConnectionStatus()
)

.lastCheckedAt(
        database.getLastCheckedAt()
)

                                .build()
                )
                .collect(Collectors.toList());
    }

   @Override
public ConnectionTestResponse testConnection(
        UUID databaseId
) {

    MonitoredDatabase database =
            monitoredDatabaseRepository
                    .findById(databaseId)
                    .orElseThrow(() ->
                            new RuntimeException(
                                    "Database not found"
                            )
                    );

    try {

        if (
            database.getDatabaseType()
            != DatabaseType.POSTGRESQL
            &&
            database.getDatabaseType()
            != DatabaseType.AURORA_POSTGRESQL
        ) {

            database.setConnectionStatus(
                    "FAILED"
            );

            database.setLastCheckedAt(
                    LocalDateTime.now()
            );

            monitoredDatabaseRepository
                    .save(database);

            return new ConnectionTestResponse(
                    false,
                    "Currently only PostgreSQL is supported"
            );
        }

        String password =
                aesEncryptionService.decrypt(
                        database.getPassword()
                );

        String jdbcUrl =
                "jdbc:postgresql://"
                + database.getHost()
                + ":"
                + database.getPort()
                + "/"
                + database.getDatabaseName();

        Connection connection =
                DriverManager.getConnection(
                        jdbcUrl,
                        database.getUsername(),
                        password
                );

        connection.close();

        database.setConnectionStatus(
                "CONNECTED"
        );

        database.setLastCheckedAt(
                LocalDateTime.now()
        );

        monitoredDatabaseRepository
                .save(database);

        return new ConnectionTestResponse(
                true,
                "Database connection successful"
        );

    } catch (Exception ex) {

        database.setConnectionStatus(
                "FAILED"
        );

        database.setLastCheckedAt(
                LocalDateTime.now()
        );

        monitoredDatabaseRepository
                .save(database);

        return new ConnectionTestResponse(
                false,
                ex.getMessage()
        );
    }
}
}