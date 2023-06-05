package com.robomatic.core.v1.services.impl;

import com.robomatic.core.v1.clients.TestExecutorClient;
import com.robomatic.core.v1.dtos.QueuesDto;
import com.robomatic.core.v1.entities.TestCaseEntity;
import com.robomatic.core.v1.entities.TestEntity;
import com.robomatic.core.v1.entities.TestExecutionEntity;
import com.robomatic.core.v1.enums.ActionEnum;
import com.robomatic.core.v1.enums.StatusEnum;
import com.robomatic.core.v1.exceptions.InternalErrorException;
import com.robomatic.core.v1.exceptions.NotFoundException;
import com.robomatic.core.v1.exceptions.messages.InternalErrorCode;
import com.robomatic.core.v1.exceptions.messages.NotFoundErrorCode;
import com.robomatic.core.v1.jms.JmsSender;
import com.robomatic.core.v1.mappers.TestExecutionMapper;
import com.robomatic.core.v1.models.TestExecutionModel;
import com.robomatic.core.v1.repositories.TestCaseRepository;
import com.robomatic.core.v1.repositories.TestExecutionRepository;
import com.robomatic.core.v1.repositories.TestRepository;
import com.robomatic.core.v1.services.ActionService;
import com.robomatic.core.v1.services.ExecuteTestService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.robomatic.core.v1.exceptions.messages.NotFoundErrorCode.E404001;

@Slf4j
@Service
public class ExecuteTestServiceImpl implements ExecuteTestService {

    @Autowired
    private TestRepository testRepository;

    @Autowired
    private TestCaseRepository testCaseRepository;

    @Autowired
    private TestExecutionRepository testExecutionRepository;

    @Autowired
    private TestExecutionMapper testExecutionMapper;

    @Autowired
    private JmsSender jmsSender;

    @Autowired
    private QueuesDto queuesDto;

    @Autowired
    private TestExecutorClient testExecutorClient;

    @Autowired
    private ActionService actionService;

    @Override
    public TestExecutionEntity executeTest(Integer testId, Integer testCaseId) {

        TestEntity testEntity = testRepository.findById(testId).orElseThrow(() -> new NotFoundException(NotFoundErrorCode.E404002));

        TestCaseEntity testCaseEntity = testCaseRepository.findById(testCaseId).orElseThrow(() -> new NotFoundException(E404001));

        TestExecutionEntity testExecutionEntity = testExecutionMapper.createTestExecutionEntity(testId);

        TestExecutionModel testExecutionModel = testExecutionMapper.createTestExecutionModel(testEntity, testCaseEntity.getFileDir(), testExecutionEntity.getTestExecutionId());

        TestExecutionEntity savedEntity = testExecutionRepository.save(testExecutionEntity);

        actionService.createAction(1, null, ActionEnum.EXECUTE.getCode(), null, testId, testExecutionEntity.getId());

        try {
            //jmsSender.sendQueue(queuesDto.getSendToExecute(), testExecutionModel);
            testExecutorClient.executeTest(testExecutionModel);
        } catch (Exception e) {
            log.error(e.getMessage());
            savedEntity.setStatus(StatusEnum.FAILED.getCode());
            testExecutionRepository.save(savedEntity);
            throw new InternalErrorException(InternalErrorCode.E500001);
        }

        return savedEntity;
    }

    @Override
    public TestExecutionEntity executeDefaultTest(Integer testId) {

        TestCaseEntity testCaseEntity = testCaseRepository.getDefaultByTestId(testId)
                .orElseThrow(() -> new NotFoundException(E404001));

        return executeTest(testId, testCaseEntity.getId());

    }
}
