package com.querypulse.backend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "database_alerts")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DatabaseAlert {

    @Id
    @GeneratedValue
    private UUID id;

    private UUID databaseId;

    private String alertType;

    private String severity;

    @Column(length = 1000)
    private String message;

    private LocalDateTime createdAt;

    /**
     * Whether the condition that triggered this alert has cleared.
     * Open (resolved = false) alerts are used to deduplicate so the
     * scheduler does not recreate the same alert (or re-send emails)
     * on every monitoring cycle.
     */
    @Builder.Default
    @Column(nullable = false)
    private Boolean resolved = false;

    private LocalDateTime resolvedAt;

}