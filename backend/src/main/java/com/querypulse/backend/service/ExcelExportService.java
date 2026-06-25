package com.querypulse.backend.service;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.querypulse.backend.dto.DatabaseAlertResponse;
import com.querypulse.backend.dto.DatabaseHealthHistoryResponse;
import com.querypulse.backend.dto.DatabaseHealthResponse;
import com.querypulse.backend.entity.MonitoredDatabase;
import com.querypulse.backend.repository.MonitoredDatabaseRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExcelExportService {

    private final DatabaseService databaseService;
    private final MonitoredDatabaseRepository monitoredDatabaseRepository;

    public byte[] generateDatabaseReportExcel(UUID databaseId) {
        try {
            MonitoredDatabase database = monitoredDatabaseRepository
                    .findById(databaseId)
                    .orElseThrow(() -> new RuntimeException("Database not found"));

            Workbook workbook = new XSSFWorkbook();

            createDatabaseInfoSheet(workbook, database);
            createHealthMetricsSheet(workbook, databaseId);
            createHistorySheet(workbook, databaseId);
            createAlertsSheet(workbook, databaseId);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            workbook.write(baos);
            workbook.close();

            return baos.toByteArray();
        } catch (Exception e) {
            log.error("Error generating Excel report", e);
            throw new RuntimeException("Failed to generate Excel report", e);
        }
    }

    private void createDatabaseInfoSheet(Workbook workbook, MonitoredDatabase database) {
        Sheet sheet = workbook.createSheet("Database Info");
        CellStyle headerStyle = getHeaderStyle(workbook);

        Row headerRow = sheet.createRow(0);
        addCell(headerRow, 0, "Property", headerStyle);
        addCell(headerRow, 1, "Value", headerStyle);

        int rowNum = 1;
        rowNum = addRow(sheet, rowNum, "Database Name", database.getDisplayName());
        rowNum = addRow(sheet, rowNum, "Type", database.getDatabaseType().toString());
        rowNum = addRow(sheet, rowNum, "Host", database.getHost());
        rowNum = addRow(sheet, rowNum, "Port", String.valueOf(database.getPort()));
        rowNum = addRow(sheet, rowNum, "Database Name", database.getDatabaseName());
        rowNum = addRow(sheet, rowNum, "Connection Status", database.getConnectionStatus());
        rowNum = addRow(sheet, rowNum, "Monitoring Enabled", database.getMonitoringEnabled() ? "Yes" : "No");
        rowNum = addRow(sheet, rowNum, "Last Checked", database.getLastCheckedAt() != null ? database.getLastCheckedAt().toString() : "N/A");

        sheet.setColumnWidth(0, 25 * 256);
        sheet.setColumnWidth(1, 40 * 256);
    }

    private void createHealthMetricsSheet(Workbook workbook, UUID databaseId) {
        Sheet sheet = workbook.createSheet("Current Health");
        CellStyle headerStyle = getHeaderStyle(workbook);

        Row headerRow = sheet.createRow(0);
        addCell(headerRow, 0, "Metric", headerStyle);
        addCell(headerRow, 1, "Value", headerStyle);

        try {
            DatabaseHealthResponse health = databaseService.getDatabaseHealth(databaseId);
            int rowNum = 1;
            rowNum = addRow(sheet, rowNum, "Status", health.getConnectionStatus());
            rowNum = addRow(sheet, rowNum, "Version", health.getDatabaseVersion());
            rowNum = addRow(sheet, rowNum, "Size", health.getDatabaseSize());
            rowNum = addRow(sheet, rowNum, "Active Connections", String.valueOf(health.getActiveConnections()));
            rowNum = addRow(sheet, rowNum, "Table Count", String.valueOf(health.getTableCount()));
            rowNum = addRow(sheet, rowNum, "Uptime", health.getDatabaseUptime());
            rowNum = addRow(sheet, rowNum, "Last Checked", health.getLastCheckedAt());
        } catch (Exception e) {
            Row row = sheet.createRow(1);
            addCell(row, 0, "Error loading health metrics", null);
        }

        sheet.setColumnWidth(0, 25 * 256);
        sheet.setColumnWidth(1, 40 * 256);
    }

    private void createHistorySheet(Workbook workbook, UUID databaseId) {
        Sheet sheet = workbook.createSheet("History");
        CellStyle headerStyle = getHeaderStyle(workbook);

        List<DatabaseHealthHistoryResponse> history = databaseService.getDatabaseHistory(databaseId);

        Row headerRow = sheet.createRow(0);
        addCell(headerRow, 0, "Timestamp", headerStyle);
        addCell(headerRow, 1, "Status", headerStyle);
        addCell(headerRow, 2, "Connections", headerStyle);
        addCell(headerRow, 3, "Size", headerStyle);
        addCell(headerRow, 4, "Tables", headerStyle);

        int rowNum = 1;
        for (DatabaseHealthHistoryResponse h : history.stream().limit(100).toList()) {
            Row row = sheet.createRow(rowNum);
            addCell(row, 0, h.getRecordedAt(), null);
            addCell(row, 1, h.getConnectionStatus(), null);
            addCell(row, 2, String.valueOf(h.getActiveConnections()), null);
            addCell(row, 3, h.getDatabaseSize(), null);
            addCell(row, 4, "", null);
            rowNum++;
        }

        sheet.setColumnWidth(0, 20 * 256);
        sheet.setColumnWidth(1, 12 * 256);
        sheet.setColumnWidth(2, 15 * 256);
        sheet.setColumnWidth(3, 15 * 256);
        sheet.setColumnWidth(4, 12 * 256);
    }

    private void createAlertsSheet(Workbook workbook, UUID databaseId) {
        Sheet sheet = workbook.createSheet("Alerts");
        CellStyle headerStyle = getHeaderStyle(workbook);

        List<DatabaseAlertResponse> alerts = databaseService.getAlerts(databaseId);

        Row headerRow = sheet.createRow(0);
        addCell(headerRow, 0, "Type", headerStyle);
        addCell(headerRow, 1, "Severity", headerStyle);
        addCell(headerRow, 2, "Message", headerStyle);
        addCell(headerRow, 3, "Created At", headerStyle);

        int rowNum = 1;
        for (DatabaseAlertResponse alert : alerts.stream().limit(100).toList()) {
            Row row = sheet.createRow(rowNum);
            addCell(row, 0, alert.getAlertType(), null);
            addCell(row, 1, alert.getSeverity(), null);
            addCell(row, 2, alert.getMessage(), null);
            addCell(row, 3, alert.getCreatedAt(), null);
            rowNum++;
        }

        sheet.setColumnWidth(0, 20 * 256);
        sheet.setColumnWidth(1, 15 * 256);
        sheet.setColumnWidth(2, 40 * 256);
        sheet.setColumnWidth(3, 20 * 256);
    }

    private CellStyle getHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);
        return style;
    }

    private void addCell(Row row, int colNum, String value, CellStyle style) {
        Cell cell = row.createCell(colNum);
        cell.setCellValue(value != null ? value : "");
        if (style != null) {
            cell.setCellStyle(style);
        }
    }

    private int addRow(Sheet sheet, int rowNum, String label, String value) {
        Row row = sheet.createRow(rowNum);
        addCell(row, 0, label, null);
        addCell(row, 1, value, null);
        return rowNum + 1;
    }
}
