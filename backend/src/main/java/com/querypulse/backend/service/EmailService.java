package com.querypulse.backend.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    public void sendAlertEmail(
            String toEmail,
            String alertType,
            String databaseName,
            String severity,
            String message
    ) {
        try {
            SimpleMailMessage mail = new SimpleMailMessage();
            mail.setTo(toEmail);
            mail.setSubject("[QueryPulse] " + severity + " Alert: " + alertType + " - " + databaseName);

            String body = buildEmailBody(alertType, databaseName, severity, message);
            mail.setText(body);

            mail.setFrom("noreply@querypulse.local");

            mailSender.send(mail);
            log.info("Alert email sent to {} for database: {}", toEmail, databaseName);
        } catch (Exception ex) {
            log.error("Failed to send alert email to {}", toEmail, ex);
        }
    }

    public void sendRecoveryEmail(
            String toEmail,
            String alertType,
            String databaseName
    ) {
        try {
            SimpleMailMessage mail = new SimpleMailMessage();
            mail.setTo(toEmail);
            mail.setSubject("[QueryPulse] RESOLVED: " + alertType + " - " + databaseName);

            String body = String.format(
                    "Hello,\n\n" +
                            "Good news — a previously reported issue in QueryPulse has cleared.\n\n" +
                            "Recovery Details:\n" +
                            "  Database: %s\n" +
                            "  Alert Type: %s\n" +
                            "  Status: RESOLVED\n\n" +
                            "No further action is required.\n\n" +
                            "Best regards,\n" +
                            "QueryPulse Team",
                    databaseName,
                    alertType
            );
            mail.setText(body);
            mail.setFrom("noreply@querypulse.local");

            mailSender.send(mail);
            log.info("Recovery email sent to {} for database: {}", toEmail, databaseName);
        } catch (Exception ex) {
            log.error("Failed to send recovery email to {}", toEmail, ex);
        }
    }

    public void sendApprovalEmail(
            String toEmail,
            String username,
            boolean approved,
            String message,
            String role
    ) {
        try {
            SimpleMailMessage mail = new SimpleMailMessage();
            mail.setTo(toEmail);

            if (approved) {
                mail.setSubject("[QueryPulse] Your Account Has Been Approved!");
                String body = String.format(
                        "Hello %s,\n\n" +
                                "Great news! Your QueryPulse account has been approved.\n\n" +
                                "Account Details:\n" +
                                "  Username: %s\n" +
                                "  Role: %s\n\n" +
                                "Message from Admin:\n%s\n\n" +
                                "You can now log in to QueryPulse and start monitoring your databases.\n\n" +
                                "Best regards,\n" +
                                "QueryPulse Team",
                        username,
                        username,
                        role != null ? role : "MEMBER",
                        message
                );
                mail.setText(body);
            } else {
                mail.setSubject("[QueryPulse] Your Account Access Request");
                String body = String.format(
                        "Hello %s,\n\n" +
                                "Unfortunately, your access request for QueryPulse has been declined.\n\n" +
                                "Reason:\n%s\n\n" +
                                "If you have questions, please contact the administrator.\n\n" +
                                "Best regards,\n" +
                                "QueryPulse Team",
                        username,
                        message
                );
                mail.setText(body);
            }

            mail.setFrom("noreply@querypulse.local");

            mailSender.send(mail);
            log.info("Approval email sent to {} - Status: {}", toEmail, approved ? "APPROVED" : "REJECTED");
        } catch (Exception ex) {
            log.error("Failed to send approval email to {}", toEmail, ex);
        }
    }

    private String buildEmailBody(
            String alertType,
            String databaseName,
            String severity,
            String message
    ) {
        return String.format(
                "Hello,\n\n" +
                        "A %s alert has been triggered in QueryPulse.\n\n" +
                        "Alert Details:\n" +
                        "  Database: %s\n" +
                        "  Alert Type: %s\n" +
                        "  Severity: %s\n" +
                        "  Message: %s\n\n" +
                        "Please log in to QueryPulse to view more details and take action.\n\n" +
                        "Best regards,\n" +
                        "QueryPulse Team",
                severity,
                databaseName,
                alertType,
                severity,
                message
        );
    }
}
