package com.robomatic.core.v1.mappers;

import com.robomatic.core.v1.dtos.ConstantsDto;
import com.robomatic.core.v1.entities.TestCaseEntity;
import com.robomatic.core.v1.entities.TestEntity;
import com.robomatic.core.v1.enums.TestCaseEnum;
import com.robomatic.core.v1.utils.RobomaticStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TestCaseMapper {

    @Autowired
    private ConstantsDto constantsDto;

    public TestCaseEntity createTestCaseEntity(TestEntity test, TestCaseEnum testCaseEnum) {

        return TestCaseEntity.builder()
                .testId(test.getId())
                .typeId(testCaseEnum.getCode())
                .fileDir(StringUtils.join(constantsDto.getEvidenceFileDir(),
                        RobomaticStringUtils.createRandomId(constantsDto.getTestCasePrefix()),
                        constantsDto.getTestCaseTerminal()))
                .build();

    }

}
