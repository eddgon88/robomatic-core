package com.robomatic.core.v1.services;

import com.robomatic.core.v1.models.ExecutionPort;
import com.robomatic.core.v1.models.TestExecutionRecordModel;

import java.util.List;

public interface TestExecutionService {

    List<TestExecutionRecordModel> getTestExecutionList(Integer testId);

    ExecutionPort getExecutionPort(Integer testId);

}
