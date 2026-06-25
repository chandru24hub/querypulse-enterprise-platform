package com.querypulse.backend.service;

import com.lowagie.text.Document;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.Element;

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
public class PdfExportService {

    private final DatabaseService databaseService;
    private final MonitoredDatabaseRepository monitoredDatabaseRepository;

    public byte[] generateDatabaseReportPdf(UUID databaseId) {
        try {
            MonitoredDatabase database = monitoredDatabaseRepository
                    .findById(databaseId)
                    .orElseThrow(() -> new RuntimeException("Database not found"));

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            Document document = new Document();
            PdfWriter.getInstance(document, baos);
            document.open();

            addTitle(document, "Database Report: " + database.getDisplayName());
            addDatabaseInfo(document, database);

            addSection(document, "Current Health Metrics");
            try {
                DatabaseHealthResponse health = databaseService.getDatabaseHealth(databaseId);
                addHealthMetrics(document, health);
            } catch (Exception e) {
                document.add(new Paragraph("Unable to load current health metrics"));
            }

            addSection(document, "Historical Data");
            List<DatabaseHealthHistoryResponse> history = databaseService.getDatabaseHistory(databaseId);
            addHistoryTable(document, history);

            addSection(document, "Recent Alerts");
            List<DatabaseAlertResponse> alerts = databaseService.getAlerts(databaseId);
            addAlertsTable(document, alerts);

            document.close();
            return baos.toByteArray();
        } catch (Exception e) {
            log.error("Error generating PDF report", e);
            throw new RuntimeException("Failed to generate PDF report", e);
        }
    }

    private void addTitle(Document document, String title) throws Exception {
        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 24);
        Paragraph titlePara = new Paragraph(title, titleFont);
        titlePara.setAlignment(Element.ALIGN_CENTER);
        document.add(titlePara);

        Font dateFont = FontFactory.getFont(FontFactory.HELVETICA, 10);
        Paragraph datePara = new Paragraph(
                "Generated: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                dateFont
        );
        datePara.setAlignment(Element.ALIGN_CENTER);
        document.add(datePara);

        document.add(new Paragraph("\n"));
    }

    private void addSection(Document document, String title) throws Exception {
        Font sectionFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14);
        Paragraph section = new Paragraph(title, sectionFont);
        document.add(new Paragraph("\n"));
        document.add(section);
    }

    private void addDatabaseInfo(Document document, MonitoredDatabase database) throws Exception {
        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);

        addTableRow(table, "Database Name", database.getDisplayName());
        addTableRow(table, "Type", database.getDatabaseType().toString());
        addTableRow(table, "Host", database.getHost() + ":" + database.getPort());
        addTableRow(table, "Connection Status", database.getConnectionStatus());
        addTableRow(table, "Monitoring Enabled", database.getMonitoringEnabled() ? "Yes" : "No");

        document.add(table);
        document.add(new Paragraph("\n"));
    }

    private void addHealthMetrics(Document document, DatabaseHealthResponse health) throws Exception {
        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);

        addTableRow(table, "Status", health.getConnectionStatus());
        addTableRow(table, "Version", health.getDatabaseVersion());
        addTableRow(table, "Size", health.getDatabaseSize());
        addTableRow(table, "Active Connections", String.valueOf(health.getActiveConnections()));
        addTableRow(table, "Table Count", String.valueOf(health.getTableCount()));
        addTableRow(table, "Uptime", health.getDatabaseUptime());
        addTableRow(table, "Last Checked", health.getLastCheckedAt());

        document.add(table);
        document.add(new Paragraph("\n"));
    }

    private void addHistoryTable(Document document, List<DatabaseHealthHistoryResponse> history) throws Exception {
        if (history.isEmpty()) {
            document.add(new Paragraph("No historical data available"));
            document.add(new Paragraph("\n"));
            return;
        }

        PdfPTable table = new PdfPTable(4);
        table.setWidthPercentage(100);

        addHeaderCell(table, "Timestamp");
        addHeaderCell(table, "Status");
        addHeaderCell(table, "Connections");
        addHeaderCell(table, "Size");

        for (DatabaseHealthHistoryResponse h : history.stream().limit(20).toList()) {
            addCell(table, h.getRecordedAt());
            addCell(table, h.getConnectionStatus());
            addCell(table, String.valueOf(h.getActiveConnections()));
            addCell(table, h.getDatabaseSize());
        }

        document.add(table);
        document.add(new Paragraph("\n"));
    }

    private void addAlertsTable(Document document, List<DatabaseAlertResponse> alerts) throws Exception {
        if (alerts.isEmpty()) {
            document.add(new Paragraph("No alerts"));
            document.add(new Paragraph("\n"));
            return;
        }

        PdfPTable table = new PdfPTable(3);
        table.setWidthPercentage(100);

        addHeaderCell(table, "Type");
        addHeaderCell(table, "Severity");
        addHeaderCell(table, "Message");

        for (DatabaseAlertResponse alert : alerts.stream().limit(20).toList()) {
            addCell(table, alert.getAlertType());
            addCell(table, alert.getSeverity());
            addCell(table, alert.getMessage());
        }

        document.add(table);
        document.add(new Paragraph("\n"));
    }

    private void addTableRow(PdfPTable table, String label, String value) {
        Font boldFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
        PdfPCell labelCell = new PdfPCell(new Paragraph(label, boldFont));
        PdfPCell valueCell = new PdfPCell(new Paragraph(value != null ? value : ""));

        table.addCell(labelCell);
        table.addCell(valueCell);
    }

    private void addHeaderCell(PdfPTable table, String header) {
        Font boldFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
        PdfPCell cell = new PdfPCell(new Paragraph(header, boldFont));
        table.addCell(cell);
    }

    private void addCell(PdfPTable table, String value) {
        table.addCell(new PdfPCell(new Paragraph(value != null ? value : "")));
    }
}
