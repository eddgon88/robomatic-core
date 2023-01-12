package com.robomatic.core.v1.services.impl;

import com.robomatic.core.v1.dtos.QueuesDto;
import com.robomatic.core.v1.entities.TestExecutionEntity;
import com.robomatic.core.v1.enums.StatusEnum;
import com.robomatic.core.v1.exceptions.NotFoundException;
import com.robomatic.core.v1.exceptions.messages.NotFoundErrorCode;
import com.robomatic.core.v1.jms.JmsSender;
import com.robomatic.core.v1.repositories.TestExecutionRepository;
import com.robomatic.core.v1.services.StopTestExecutionService;
import org.springframework.beans.factory.annotation.Autowired;

public class StopTestExecutionServiceImpl implements StopTestExecutionService {

    @Autowired
    private JmsSender jmsSender;

    @Autowired
    private QueuesDto queuesDto;

    @Autowired
    private TestExecutionRepository testExecutionRepository;

    @Override
    public TestExecutionEntity stopTestExecution(Integer testId) {
        TestExecutionEntity testExecution = testExecutionRepository.findByTestIdAndRunningStatus(testId)
                .orElseThrow(() -> new NotFoundException(NotFoundErrorCode.E404003));

        jmsSender.sendQueue(queuesDto.getSendToExecute(), testExecution);

        testExecution.setStatus(StatusEnum.STOPPED.getCode());
        testExecutionRepository.save(testExecution);
        return testExecution;
    }
}
