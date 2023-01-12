package com.robomatic.core.v1.services;

import com.robomatic.core.v1.entities.TestExecutionEntity;

public interface StopTestExecutionService {

    TestExecutionEntity stopTestExecution(Integer testId);

}
