package com.robomatic.core.v1.services.impl;

import com.robomatic.core.v1.entities.TestEntity;
import com.robomatic.core.v1.enums.ActionEnum;
import com.robomatic.core.v1.enums.RoleEnum;
import com.robomatic.core.v1.enums.TestCaseEnum;
import com.robomatic.core.v1.exceptions.ForbiddenException;
import com.robomatic.core.v1.exceptions.InternalErrorException;
import com.robomatic.core.v1.mappers.TestMapper;
import com.robomatic.core.v1.models.CreateTestRequestModel;
import com.robomatic.core.v1.models.UserModel;
import com.robomatic.core.v1.repositories.TestRepository;
import com.robomatic.core.v1.services.ActionService;
import com.robomatic.core.v1.services.CreateTestCaseService;
import com.robomatic.core.v1.services.CreateTestService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.robomatic.core.v1.exceptions.messages.ForbiddenErrorCode.E403002;
import static com.robomatic.core.v1.exceptions.messages.InternalErrorCode.E500005;

@Slf4j
@Service
public class CreateTestServiceImpl implements CreateTestService {

    @Autowired
    protected TestMapper testRequestMapper;

    @Autowired
    protected TestRepository testRepository;

    @Autowired
    protected CreateTestCaseService createTestCaseService;

    @Autowired
    protected ActionService actionService;

    @Autowired
    private UserModel currentUser;

    @Override
    public TestEntity createTest(CreateTestRequestModel createTestRequest) {
        // Validar que el usuario tenga rol ADMIN o ANALYST
        validateUserRole();

        try {
            TestEntity testEntity = testRequestMapper.createTestEntity(createTestRequest);

            testEntity = testRepository.save(testEntity);

            createTestCaseService.createTestCase(testEntity, createTestRequest.getTestCases(), TestCaseEnum.DEFAULT);

            actionService.createAction(1, null, ActionEnum.CREATE.getCode(), null, testEntity.getId(), null);

            return testEntity;
        } catch (Exception e) {
            throw new InternalErrorException(E500005, String.format("Conflicts: %s", e.getMessage()));
        }
    }

    private void validateUserRole() {
        Integer roleId = currentUser.getRoleId();
        if (roleId == null || 
            (!roleId.equals(RoleEnum.ADMIN.getCode()) && !roleId.equals(RoleEnum.ANALYST.getCode()))) {
            throw new ForbiddenException(E403002, "Only ADMIN and ANALYST users can create tests");
        }
    }

}
