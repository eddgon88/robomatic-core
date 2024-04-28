package com.robomatic.core.v1.mappers;

import com.robomatic.core.v1.dtos.ConstantsDto;
import com.robomatic.core.v1.entities.TestEntity;
import com.robomatic.core.v1.entities.TestExecutionEntity;
import com.robomatic.core.v1.enums.StatusEnum;
import com.robomatic.core.v1.models.TestExecutionModel;
import com.robomatic.core.v1.utils.RobomaticStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TestExecutionMapper {

    @Autowired
    private ConstantsDto constantsDto;

    public TestExecutionModel createTestExecutionModel(TestEntity testEntity, String testCaseFileDir, String testExecutionId) {

        return TestExecutionModel.builder()
                .script(testEntity.getScript())
                .beforeScript(testEntity.getBeforeScript())
                .afterScript(testEntity.getAfterScript())
                .testCasesFile(testCaseFileDir)
                .threads(testEntity.getThreads())
                .name(testEntity.getName())
                .testExecutionId(testExecutionId)
                .web(testEntity.isWeb())
                .build();

    }

    public TestExecutionEntity createTestExecutionEntity(Integer testId) {

        String testExecutionId = RobomaticStringUtils.createRandomId(constantsDto.getTestExecutionPrefix());

        return TestExecutionEntity.builder()
                .testExecutionId(testExecutionId)
                .testId(testId)
                .testResultsDir(RobomaticStringUtils.join(constantsDto.getEvidenceFileDir(), testExecutionId))
                .status(StatusEnum.RUNNING.getCode())
                .build();

    }

}
