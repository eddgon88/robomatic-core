package com.robomatic.core.v1.mappers;

import com.robomatic.core.v1.entities.CaseExecutionEntity;
import com.robomatic.core.v1.models.CreateCaseExecutionRequestModel;
import org.springframework.stereotype.Component;

@Component
public class CaseExecutionMapper {


    public CaseExecutionEntity createCaseExecutionEntity(CreateCaseExecutionRequestModel caseExecutionRequestModel) {
        return CaseExecutionEntity.builder()
                .caseExecutionId(caseExecutionRequestModel.getCaseExecutionId())
                .testExecutionId(caseExecutionRequestModel.getTestExecutionId())
                .status(caseExecutionRequestModel.getStatus())
                .testResultsDir(caseExecutionRequestModel.getCaseResultsDir())
                .build();
    }

}
