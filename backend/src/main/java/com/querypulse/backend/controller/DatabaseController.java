package com.querypulse.backend.controller;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.querypulse.backend.dto.CreateDatabaseRequest;
import com.querypulse.backend.dto.DatabaseResponse;
import com.querypulse.backend.entity.MonitoredDatabase;
import com.querypulse.backend.service.DatabaseService;
import java.util.UUID;
import com.querypulse.backend.dto.ConnectionTestResponse;
import com.querypulse.backend.dto.DatabaseHealthResponse;
import lombok.RequiredArgsConstructor;

import com.querypulse.backend.dto.DatabaseHealthHistoryResponse;
import com.querypulse.backend.dto.QueryAnalyzerResponse;
import com.querypulse.backend.dto.SlowQueryResponse;
import com.querypulse.backend.dto.DatabaseAlertResponse;

@RestController
@RequestMapping("/api/databases")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class DatabaseController {

    private final DatabaseService
            databaseService;

    @PostMapping
    public MonitoredDatabase createDatabase(

            @RequestBody
            CreateDatabaseRequest request
    ) {

        return databaseService
                .createDatabase(request);
    }

    @GetMapping
    public List<DatabaseResponse>
    getAllDatabases() {

        return databaseService
                .getAllDatabases();
    }

    @PostMapping("/{id}/test-connection")
public ConnectionTestResponse testConnection(

        @PathVariable
        UUID id
) {

    return databaseService
            .testConnection(id);
}

@GetMapping("/{id}/health")
public DatabaseHealthResponse getDatabaseHealth(

        @PathVariable
        UUID id
) {

    return databaseService
            .getDatabaseHealth(id);
}

@GetMapping("/{id}/history")
public List<DatabaseHealthHistoryResponse>
getDatabaseHistory(

        @PathVariable
        UUID id
) {

    return databaseService
            .getDatabaseHistory(
                    id
            );
}

@GetMapping("/{id}/query-analysis")
public QueryAnalyzerResponse
getQueryAnalysis(

        @PathVariable
        UUID id
) {

    return databaseService
            .getQueryAnalysis(
                    id
            );
}

@GetMapping("/{id}/slow-queries")
public List<SlowQueryResponse>
getSlowQueries(

        @PathVariable
        UUID id
) {

    return databaseService
            .getSlowQueries(
                    id
            );
}

@GetMapping("/{id}/alerts")
public List<DatabaseAlertResponse>
getAlerts(

        @PathVariable
        UUID id
) {

    return databaseService
            .getAlerts(
                    id
            );

}
}