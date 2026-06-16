package com.querypulse.backend.service.impl;

import java.sql.Connection;
import java.sql.DriverManager;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import com.querypulse.backend.repository.DatabaseHealthHistoryRepository;
import com.querypulse.backend.security.AesEncryptionService;
import org.springframework.stereotype.Service;
import com.querypulse.backend.entity.DatabaseHealthHistory;
import com.querypulse.backend.dto.QueryAnalyzerResponse;

import com.querypulse.backend.dto.CreateDatabaseRequest;
import com.querypulse.backend.dto.DatabaseResponse;
import com.querypulse.backend.dto.ConnectionTestResponse;
import com.querypulse.backend.entity.MonitoredDatabase;
import com.querypulse.backend.enums.DatabaseType;
import com.querypulse.backend.repository.MonitoredDatabaseRepository;
import com.querypulse.backend.service.DatabaseService;

import java.sql.Statement;
import java.sql.ResultSet;

import com.querypulse.backend.dto.DatabaseHealthResponse;

import lombok.RequiredArgsConstructor;

import java.util.stream.Collectors;

import com.querypulse.backend.dto.DatabaseHealthHistoryResponse;
import com.querypulse.backend.entity.DatabaseHealthHistory;

@Service
@RequiredArgsConstructor
public class DatabaseServiceImpl
implements DatabaseService {

    private final MonitoredDatabaseRepository
            monitoredDatabaseRepository;

    private final AesEncryptionService
            aesEncryptionService;

 private final DatabaseHealthHistoryRepository
        databaseHealthHistoryRepository;

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

@Override
public DatabaseHealthResponse getDatabaseHealth(
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

        String version = "Unknown";

        Statement versionStatement =
                connection.createStatement();

        ResultSet versionResult =
                versionStatement.executeQuery(
                        "SELECT version()"
                );

        if (versionResult.next()) {

            version =
                    versionResult.getString(1);
        }

        String size = "Unknown";

        Statement sizeStatement =
                connection.createStatement();

        ResultSet sizeResult =
                sizeStatement.executeQuery(
                        """
                        SELECT pg_size_pretty(
                        pg_database_size(
                        current_database()))
                        """
                );

        if (sizeResult.next()) {

            size =
                    sizeResult.getString(1);
        }

        Integer activeConnections = 0;

        Statement connectionStatement =
                connection.createStatement();

        ResultSet connectionResult =
                connectionStatement.executeQuery(
                        """
                        SELECT count(*)
                        FROM pg_stat_activity
                        WHERE state='active'
                        """
                );

        if (connectionResult.next()) {

            activeConnections =
                    connectionResult.getInt(1);
        }

        String uptime = "-";

        Statement uptimeStatement =
                connection.createStatement();

        ResultSet uptimeResult =
                uptimeStatement.executeQuery(
                        """
                        SELECT now() - pg_postmaster_start_time()
                        """
                );

        if (uptimeResult.next()) {

            uptime =
                    uptimeResult.getString(1);
        }

        Integer tableCount = 0;

        Statement tableStatement =
                connection.createStatement();

        ResultSet tableResult =
                tableStatement.executeQuery(
                        """
                        SELECT count(*)
                        FROM information_schema.tables
                        WHERE table_schema='public'
                        """
                );

        if (tableResult.next()) {

            tableCount =
                    tableResult.getInt(1);
        }

        versionResult.close();
        versionStatement.close();

        sizeResult.close();
        sizeStatement.close();

        connectionResult.close();
        connectionStatement.close();

        uptimeResult.close();
        uptimeStatement.close();

        tableResult.close();
        tableStatement.close();

        connection.close();

        return new DatabaseHealthResponse(

                version,

                size,

                activeConnections,

                database.getConnectionStatus(),

                database.getLastCheckedAt() != null
                        ? database.getLastCheckedAt().toString()
                        : "-",

                uptime,

                tableCount
        );

    } catch (Exception ex) {

        ex.printStackTrace();

        throw new RuntimeException(
                ex.getMessage()
        );
    }
}

@Override
public void refreshDatabaseMetrics(
        UUID databaseId
) {

    DatabaseHealthResponse health =
            getDatabaseHealth(
                    databaseId
            );

    MonitoredDatabase database =
            monitoredDatabaseRepository
                    .findById(databaseId)
                    .orElseThrow(
                            () -> new RuntimeException(
                                    "Database not found"
                            )
                    );

    database.setActiveConnections(
            health.getActiveConnections()
    );

    database.setDatabaseSize(
            health.getDatabaseSize()
    );

    database.setDatabaseUptime(
            health.getDatabaseUptime()
    );

    database.setTableCount(
            health.getTableCount()
    );

    database.setLastCheckedAt(
            LocalDateTime.now()
    );

    monitoredDatabaseRepository.save(
            database
    );
}

@Override
public void saveHealthHistory(
        UUID databaseId
) {

    DatabaseHealthResponse health =
            getDatabaseHealth(
                    databaseId
            );

    DatabaseHealthHistory history =
            DatabaseHealthHistory
                    .builder()

                    .databaseId(
                            databaseId
                    )

                    .recordedAt(
                            LocalDateTime.now()
                    )

                    .connectionStatus(
                            health.getConnectionStatus()
                    )

                    .activeConnections(
                            health.getActiveConnections()
                    )

                    .databaseSize(
                            health.getDatabaseSize()
                    )

                    .databaseUptime(
                            health.getDatabaseUptime()
                    )

                    .tableCount(
                            health.getTableCount()
                    )

                    .build();

    databaseHealthHistoryRepository
            .save(
                    history
            );
}

@Override
public List<DatabaseHealthHistoryResponse>
getDatabaseHistory(
        UUID databaseId
) {

    return databaseHealthHistoryRepository
            .findByDatabaseIdOrderByRecordedAtAsc(
                    databaseId
            )
            .stream()
            .map(history ->

                    new DatabaseHealthHistoryResponse(

                            history
                                    .getRecordedAt()
                                    .toString(),

                            history
                                    .getActiveConnections(),

                            history
                                    .getDatabaseSize(),

                            history
                                    .getConnectionStatus()
                    )
            )
            .collect(
                    Collectors.toList()
            );
}
@Override
public QueryAnalyzerResponse getQueryAnalysis(
        UUID databaseId
) {

    MonitoredDatabase database =
            monitoredDatabaseRepository
                    .findById(databaseId)
                    .orElseThrow(
                            () -> new RuntimeException(
                                    "Database not found"
                            )
                    );

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

    int activeSessions = 0;
    int idleSessions = 0;
    int totalSessions = 0;

    try (

            Connection connection =
                    DriverManager.getConnection(
                            jdbcUrl,
                            database.getUsername(),
                            password
                    );

            Statement statement =
                    connection.createStatement();

            ResultSet resultSet =
                    statement.executeQuery(
                            """
                            SELECT state
                            FROM pg_stat_activity
                            """
                    )

    ) {

        while (resultSet.next()) {

            totalSessions++;

            String state =
                    resultSet.getString(
                            "state"
                    );

            if (
                    "active".equalsIgnoreCase(
                            state
                    )
            ) {

                activeSessions++;

            } else if (
                    "idle".equalsIgnoreCase(
                            state
                    )
            ) {

                idleSessions++;

            }

        }

    } catch (Exception ex) {

        throw new RuntimeException(
                ex.getMessage()
        );

    }

    return QueryAnalyzerResponse
            .builder()
            .activeSessions(
                    activeSessions
            )
            .idleSessions(
                    idleSessions
            )
            .totalSessions(
                    totalSessions
            )
            .build();

}

}