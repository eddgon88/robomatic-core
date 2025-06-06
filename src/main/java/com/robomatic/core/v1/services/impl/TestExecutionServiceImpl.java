package com.robomatic.core.v1.services.impl;

import com.robomatic.core.v1.clients.TestExecutorClient;
import com.robomatic.core.v1.entities.ActionRelationalEntity;
import com.robomatic.core.v1.entities.TestExecutionEntity;
import com.robomatic.core.v1.exceptions.NotFoundException;
import com.robomatic.core.v1.exceptions.messages.NotFoundErrorCode;
import com.robomatic.core.v1.models.ExecutionPort;
import com.robomatic.core.v1.models.TestExecutionRecordModel;
import com.robomatic.core.v1.models.UserModel;
import com.robomatic.core.v1.repositories.ActionRelationalRepository;
import com.robomatic.core.v1.repositories.TestExecutionRepository;
import com.robomatic.core.v1.repositories.UserRepository;
import com.robomatic.core.v1.services.TestExecutionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Slf4j
@Service
public class TestExecutionServiceImpl implements TestExecutionService {

    @Autowired
    private TestExecutionRepository testExecutionRepository;

    @Autowired
    private ActionRelationalRepository actionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TestExecutorClient testExecutorClient;

    @Override
    public List<TestExecutionRecordModel> getTestExecutionList(Integer testId) {
        log.info("Getting list of executions for test: {}", testId);
        try {
            List<TestExecutionRecordModel> ret = new ArrayList<>();
            List<TestExecutionEntity> testExecutionEntities = testExecutionRepository.findByTestId(testId);
            testExecutionEntities.forEach(t -> {
                ActionRelationalEntity actionEntity = actionRepository.findExecutionActionByTestExecution(t.getId())
                        .orElseThrow(() -> new NotFoundException(NotFoundErrorCode.E404003));
                ret.add(TestExecutionRecordModel.builder()
                        .id(t.getId())
                        .testExecutionId(t.getTestExecutionId())
                        .testId(t.getTestId())
                        .testResultDir(t.getTestResultsDir())
                        .status(t.getStatus())
                        .date(actionEntity.getDate())
                        .user(actionEntity.getUserFrom().getFullName())
                        .build());
            });
            ret.sort(Comparator.comparing(TestExecutionRecordModel::getDate, Comparator.reverseOrder()));
            return ret;
        } catch (Exception e) {
            log.error("Exception getting list of executions - {}", e.getMessage());
            throw e;
        }
    }

    @Override
    public ExecutionPort getExecutionPort(Integer testId) {
        log.info("getting execution ports: {}", testId);
        TestExecutionEntity testExecution = testExecutionRepository.findByTestIdAndRunningStatus(testId)
                .orElseThrow(() -> new NotFoundException(NotFoundErrorCode.E404003));
        log.info("getting execution ports: {}", testExecution.getTestExecutionId());

        return testExecutorClient.getExecutionPorts(testExecution.getTestExecutionId());
    }
}
