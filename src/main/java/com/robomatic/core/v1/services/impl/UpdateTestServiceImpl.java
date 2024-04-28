package com.robomatic.core.v1.services.impl;

import com.robomatic.core.v1.entities.TestEntity;
import com.robomatic.core.v1.enums.ActionEnum;
import com.robomatic.core.v1.exceptions.InternalErrorException;
import com.robomatic.core.v1.exceptions.NotFoundException;
import com.robomatic.core.v1.mappers.TestMapper;
import com.robomatic.core.v1.models.UpdateTestRequestModel;
import com.robomatic.core.v1.repositories.TestRepository;
import com.robomatic.core.v1.services.ActionService;
import com.robomatic.core.v1.services.UpdateTestCaseService;
import com.robomatic.core.v1.services.UpdateTestService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.robomatic.core.v1.exceptions.messages.InternalErrorCode.E500004;
import static com.robomatic.core.v1.exceptions.messages.NotFoundErrorCode.E404002;

@Slf4j
@Service
public class UpdateTestServiceImpl implements UpdateTestService {

    @Autowired
    private TestMapper testMapper;

    @Autowired
    private UpdateTestCaseService updateTestCaseService;

    @Autowired
    private TestRepository testRepository;

    @Autowired
    private ActionService actionService;

    @Override
    public TestEntity updateTest(UpdateTestRequestModel updateTestRequest) {

        try {
            TestEntity testEntity = testRepository.findById(updateTestRequest.getId()).orElseThrow(() -> new NotFoundException(E404002));

            testEntity.setFolderId(updateTestRequest.getFolderId());
            testEntity.setName(updateTestRequest.getName());
            testEntity.setScript(updateTestRequest.getScript());
            testEntity.setBeforeScript(updateTestRequest.getBeforeScript());
            testEntity.setAfterScript(updateTestRequest.getAfterScript());
            testEntity.setThreads(updateTestRequest.getThreads());
            testEntity.setWeb(updateTestRequest.isWeb());
            testEntity.setDescription(updateTestRequest.getDescription());

            updateTestCaseService.updateTestCase(updateTestRequest.getTestCaseId(),
                    updateTestRequest.getTestCases());

            actionService.createAction(1, null, ActionEnum.UPDATE.getCode(), null, testEntity.getId(), null);

            return testRepository.save(testEntity);
        } catch (Exception e) {
            throw new InternalErrorException(E500004, String.format("Conflicts: %s", e.getMessage()));
        }

    }


}
