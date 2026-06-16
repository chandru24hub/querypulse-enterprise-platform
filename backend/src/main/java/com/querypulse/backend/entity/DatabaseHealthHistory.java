package com.querypulse.backend.entity;

import jakarta.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "database_health_history")

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DatabaseHealthHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "database_id")
    private UUID databaseId;

    @Column(name = "recorded_at")
    private LocalDateTime recordedAt;

    @Column(name = "connection_status")
    private String connectionStatus;

    @Column(name = "active_connections")
    private Integer activeConnections;

    @Column(name = "database_size")
    private String databaseSize;

    @Column(name = "database_uptime")
    private String databaseUptime;

    @Column(name = "table_count")
    private Integer tableCount;

}