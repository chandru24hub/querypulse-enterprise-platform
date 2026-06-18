package com.querypulse.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SlowQueryResponse {

    private Integer pid;

    private String username;

    private String databaseName;

    private String state;

    private String runningTime;

    private String query;

}