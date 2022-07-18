package com.robomatic.core.v1.services;

import com.robomatic.core.v1.entities.CaseExecutionEntity;
import com.robomatic.core.v1.models.CreateCaseExecutionRequestModel;

public interface CreateCaseExecutionService {

    CaseExecutionEntity createCaseExecution(CreateCaseExecutionRequestModel createCaseExecutionRequest);

}
