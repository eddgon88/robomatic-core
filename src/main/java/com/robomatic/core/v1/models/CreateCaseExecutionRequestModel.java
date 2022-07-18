package com.robomatic.core.v1.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateCaseExecutionRequestModel {

    private String caseExecutionId;
    private String testExecutionId;
    private String caseResultsDir;
    private String status;

}
