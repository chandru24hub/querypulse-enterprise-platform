package com.querypulse.backend.controller;

import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.MediaType;
import org.springframework.http.HttpHeaders;

import com.querypulse.backend.dto.CreateDatabaseRequest;
import com.querypulse.backend.dto.DatabaseResponse;
import com.querypulse.backend.dto.QueryOptimizationResponse;
import com.querypulse.backend.entity.MonitoredDatabase;
import com.querypulse.backend.service.DatabaseService;
import com.querypulse.backend.service.PdfExportService;
import com.querypulse.backend.service.ExcelExportService;
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

    private final PdfExportService
            pdfExportService;

    private final ExcelExportService
            excelExportService;

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

@GetMapping("/all/alerts")
public List<DatabaseAlertResponse>
getAllAlerts() {

    return databaseService.getAllAlerts();
}

@GetMapping("/{id}/export/pdf")
public ResponseEntity<byte[]> exportDatabasePdf(

        @PathVariable
        UUID id
) {

    byte[] pdfBytes = pdfExportService.generateDatabaseReportPdf(id);

    return ResponseEntity
            .ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=database-report.pdf")
            .contentType(MediaType.APPLICATION_PDF)
            .body(pdfBytes);
}

@GetMapping("/{id}/export/excel")
public ResponseEntity<byte[]> exportDatabaseExcel(

        @PathVariable
        UUID id
) {

    byte[] excelBytes = excelExportService.generateDatabaseReportExcel(id);

    return ResponseEntity
            .ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=database-report.xlsx")
            .contentType(MediaType.valueOf("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
            .body(excelBytes);
}

@PostMapping("/query/optimize")
public QueryOptimizationResponse optimizeQuery(

        @RequestBody
        Map<String, String> request
) {

    String sqlQuery = request.get("query");
    if (sqlQuery == null || sqlQuery.trim().isEmpty()) {
        return QueryOptimizationResponse.builder()
                .suggestions("Please provide a valid SQL query")
                .success(false)
                .build();
    }

    String suggestions = databaseService.getQueryOptimizationSuggestions(sqlQuery);

    return QueryOptimizationResponse.builder()
            .suggestions(suggestions)
            .success(true)
            .build();
}
}