package com.querypulse.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DatabaseHealthHistoryResponse {

    private String recordedAt;

    private Integer activeConnections;

    private String databaseSize;

    private String connectionStatus;

}