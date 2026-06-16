package com.querypulse.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QueryAnalyzerResponse {

    private Integer activeSessions;

    private Integer idleSessions;

    private Integer totalSessions;

}