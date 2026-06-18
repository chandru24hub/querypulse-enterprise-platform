package com.querypulse.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DatabaseAlertResponse {

    private String severity;

    private String alertType;

    private String message;

    private String createdAt;

}