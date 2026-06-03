package com.querypulse.backend.service;

import java.util.List;

import com.querypulse.backend.dto.CreateDatabaseRequest;
import com.querypulse.backend.dto.DatabaseResponse;
import com.querypulse.backend.entity.MonitoredDatabase;

public interface DatabaseService {

    MonitoredDatabase createDatabase(
            CreateDatabaseRequest request
    );

    List<DatabaseResponse> getAllDatabases();
}