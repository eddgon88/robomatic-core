package com.robomatic.core.v1.services;

import com.robomatic.core.v1.entities.TestExecutionEntity;

public interface JmsExecuteTestService {

    TestExecutionEntity executeTest(Integer testId, Integer testCaseId);

    TestExecutionEntity executeDefaultTest(Integer testId);

}
