package com.querypulse.backend.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import com.querypulse.backend.enums.DatabaseType;

import jakarta.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "monitored_databases")

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MonitoredDatabase {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String displayName;

    @Enumerated(EnumType.STRING)
    private DatabaseType databaseType;

    private String host;

    private Integer port;

    private String databaseName;

    private String username;

    private String password;

    private Boolean monitoringEnabled;

    private LocalDateTime createdAt;
}