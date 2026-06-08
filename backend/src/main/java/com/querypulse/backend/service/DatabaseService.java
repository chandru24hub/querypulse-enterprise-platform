package com.querypulse.backend.service;

import java.util.List;
import java.util.UUID;

import com.querypulse.backend.dto.ConnectionTestResponse;
import com.querypulse.backend.dto.CreateDatabaseRequest;
import com.querypulse.backend.dto.DatabaseHealthResponse;
import com.querypulse.backend.dto.DatabaseResponse;
import com.querypulse.backend.entity.MonitoredDatabase;

public interface DatabaseService {

    MonitoredDatabase createDatabase(
            CreateDatabaseRequest request
    );

    List<DatabaseResponse> getAllDatabases();

    ConnectionTestResponse testConnection(
            UUID databaseId
    );

    DatabaseHealthResponse getDatabaseHealth(
            UUID databaseId
    );
}