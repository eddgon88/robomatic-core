package com.robomatic.core.v1.services.impl;

import com.robomatic.core.v1.entities.TestExecutionEntity;
import com.robomatic.core.v1.enums.StatusEnum;
import com.robomatic.core.v1.exceptions.NotFoundException;
import com.robomatic.core.v1.exceptions.messages.NotFoundErrorCode;
import com.robomatic.core.v1.models.UpdateTestExecutionRequestModel;
import com.robomatic.core.v1.repositories.TestExecutionRepository;
import com.robomatic.core.v1.services.UpdateTestExecutionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class UpdateTestExecutionServiceImpl implements UpdateTestExecutionService {

    @Autowired
    TestExecutionRepository testExecutionRepository;

    @Override
    public TestExecutionEntity updateCaseExecution(UpdateTestExecutionRequestModel updateTestExecutionRequest) {
        TestExecutionEntity testExecutionEntity = testExecutionRepository.findByTestExecutionId(updateTestExecutionRequest.getTestExecutionId())
                .orElseThrow(() -> new NotFoundException(NotFoundErrorCode.E404003));

        testExecutionEntity.setStatus(StatusEnum.getMethodByValue(updateTestExecutionRequest
                .getStatus()).getCode());

        return testExecutionRepository.save(testExecutionEntity);
    }
}
