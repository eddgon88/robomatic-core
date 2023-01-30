package com.robomatic.core.v1.services.impl;

import com.robomatic.core.v1.clients.TestExecutorClient;
import com.robomatic.core.v1.dtos.QueuesDto;
import com.robomatic.core.v1.entities.TestExecutionEntity;
import com.robomatic.core.v1.enums.StatusEnum;
import com.robomatic.core.v1.exceptions.NotFoundException;
import com.robomatic.core.v1.exceptions.messages.NotFoundErrorCode;
import com.robomatic.core.v1.jms.JmsSender;
import com.robomatic.core.v1.repositories.TestExecutionRepository;
import com.robomatic.core.v1.services.StopTestExecutionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class StopTestExecutionServiceImpl implements StopTestExecutionService {

    @Autowired
    private JmsSender jmsSender;

    @Autowired
    private QueuesDto queuesDto;

    @Autowired
    private TestExecutionRepository testExecutionRepository;

    @Autowired
    private TestExecutorClient testExecutorClient;

    @Override
    public TestExecutionEntity stopTestExecution(Integer testId) {
        log.info("Stopping test: {}", testId);
        TestExecutionEntity testExecution = testExecutionRepository.findByTestIdAndRunningStatus(testId)
                .orElseThrow(() -> new NotFoundException(NotFoundErrorCode.E404003));
        log.info("Stopping test execution: {}", testExecution.getTestExecutionId());

        //jmsSender.sendQueue(queuesDto.getStopTestExecution(), testExecution);
        testExecutorClient.stopTestExecution(testExecution);

        testExecution.setStatus(StatusEnum.STOPPED.getCode());
        testExecutionRepository.save(testExecution);
        log.info("Test Execution stopped");
        return testExecution;
    }
}
