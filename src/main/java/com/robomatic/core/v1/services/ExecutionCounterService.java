package com.robomatic.core.v1.services;

import com.robomatic.core.v1.entities.ExecutionCountEntity;
import com.robomatic.core.v1.models.ExecutionCountMessage;
import com.robomatic.core.v1.models.TestExecutionModel;

public interface ExecutionCounterService {
    void retrieveAndAttachCounterData(Integer testId, TestExecutionModel model);
    void incrementExecutionCount(ExecutionCountMessage message);
    ExecutionCountEntity getCounterData(Integer testId, Integer year, Integer month);
    void updateMaxExecutions(Integer testId, Integer maxExecutions, Integer year, Integer month);
}
