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

}