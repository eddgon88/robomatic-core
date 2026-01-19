package com.robomatic.core.v1.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleModel {

    private Long id;
    private String scheduleId;
    private Integer testId;
    private String testName;
    private String name;
    private String description;
    private String triggerType;
    private Map<String, Object> expression;
    private String status;
    private Integer statusCode;
    private LocalDateTime nextRunTime;
    private LocalDateTime lastRunTime;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}


