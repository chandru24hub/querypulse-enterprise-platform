package com.querypulse.backend.dto;

import com.querypulse.backend.enums.DatabaseType;

import lombok.Data;

@Data
public class CreateDatabaseRequest {

    private String displayName;

    private DatabaseType databaseType;

    private String host;

    private Integer port;

    private String databaseName;

    private String username;

    private String password;
}