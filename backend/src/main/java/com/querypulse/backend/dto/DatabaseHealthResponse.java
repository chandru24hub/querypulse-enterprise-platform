package com.querypulse.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DatabaseHealthResponse {

    private String databaseVersion;

    private String databaseSize;

    private String connectionStatus;

    private String lastCheckedAt;
}