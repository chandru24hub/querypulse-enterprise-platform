package com.querypulse.backend.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import com.querypulse.backend.enums.DatabaseType;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DatabaseResponse {

    private UUID id;

    private String displayName;

    private DatabaseType databaseType;

    private String host;

    private Integer port;

    private String databaseName;

    private String username;

    private Boolean monitoringEnabled;

    private LocalDateTime createdAt;
}