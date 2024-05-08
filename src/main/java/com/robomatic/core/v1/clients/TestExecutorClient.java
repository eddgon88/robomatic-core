package com.robomatic.core.v1.clients;

import com.robomatic.core.v1.entities.TestExecutionEntity;
import com.robomatic.core.v1.models.ExecutionPort;
import com.robomatic.core.v1.models.TestExecutionModel;

public interface TestExecutorClient {

    void executeTest(TestExecutionModel testExecutionModel);

    void stopTestExecution(TestExecutionEntity testExecution);

    ExecutionPort getExecutionPorts(String executionId);

}
