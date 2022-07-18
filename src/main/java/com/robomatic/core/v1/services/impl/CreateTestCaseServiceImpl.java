package com.robomatic.core.v1.services.impl;

import com.robomatic.core.v1.entities.TestCaseEntity;
import com.robomatic.core.v1.entities.TestEntity;
import com.robomatic.core.v1.enums.TestCaseEnum;
import com.robomatic.core.v1.exceptions.InternalErrorException;
import com.robomatic.core.v1.mappers.TestCaseMapper;
import com.robomatic.core.v1.repositories.TestCaseRepository;
import com.robomatic.core.v1.services.CreateTestCaseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Base64;

import static com.robomatic.core.v1.exceptions.messages.InternalErrorCode.E500002;

@Slf4j
@Service
public class CreateTestCaseServiceImpl implements CreateTestCaseService {

    @Autowired
    private TestCaseRepository testCaseRepository;

    @Autowired
    private TestCaseMapper testCaseMapper;

    @Override
    public TestCaseEntity createTestCase(TestEntity test, String testCases, TestCaseEnum testCaseEnum) {
        try {
            TestCaseEntity testCaseEntity = testCaseMapper.createTestCaseEntity(test, testCaseEnum);

            createTestCaseFile(testCaseEntity.getFileDir(), testCases);

            return testCaseRepository.save(testCaseEntity);
        } catch (Exception e) {
            throw new InternalErrorException(E500002, String.format("Conflicts: %s", e.getMessage()));
        }
    }

    private void createTestCaseFile(String fileDir, String testCases) throws IOException {

        File newFile = new File(fileDir);
        boolean success = newFile.createNewFile();
        if (success) {
            BufferedWriter writer = new BufferedWriter(new FileWriter(fileDir));
            byte[] decodedBytes = Base64.getDecoder().decode(testCases);
            writer.write(new String(decodedBytes));
            writer.close();
        }
    }
}
