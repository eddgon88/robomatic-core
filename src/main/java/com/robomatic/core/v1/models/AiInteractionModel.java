package com.robomatic.core.v1.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiInteractionModel {
    private Integer id;
    private Integer testExecutionId;
    private String agentName;
    private String prompt;
    private String response;
    private String status;
    private String errorMessage;
    private Integer tokenUsage;
    private LocalDateTime timestamp;
}
