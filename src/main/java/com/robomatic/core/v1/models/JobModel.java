package com.robomatic.core.v1.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobModel {

    private String jobId;
    private String triggerType;
    private Map<String, Object> expression;
    private String queue;
    private String message;
    private String nextRunTime;
    private String trigger;

}
