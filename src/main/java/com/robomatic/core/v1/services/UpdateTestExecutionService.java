package com.robomatic.core.v1.services;

import com.robomatic.core.v1.entities.CaseExecutionEntity;
import com.robomatic.core.v1.entities.TestExecutionEntity;
import com.robomatic.core.v1.models.UpdateTestExecutionRequestModel;

public interface UpdateTestExecutionService {

    TestExecutionEntity updateCaseExecution(UpdateTestExecutionRequestModel updateTestExecutionRequest);

}
