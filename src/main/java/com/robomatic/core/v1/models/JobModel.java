package com.robomatic.core.v1.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobModel {

    private String jobId;
    private String triggerType;
    private String expression;
    private String queue;
    private String message;

}
