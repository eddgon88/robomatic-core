package com.robomatic.core.v1.services.impl;

import com.robomatic.core.v1.entities.TestCaseEntity;
import com.robomatic.core.v1.entities.TestEntity;
import com.robomatic.core.v1.enums.TestCaseEnum;
import com.robomatic.core.v1.exceptions.InternalErrorException;
import com.robomatic.core.v1.mappers.TestCaseMapper;
import com.robomatic.core.v1.repositories.TestCaseRepository;
import com.robomatic.core.v1.services.CreateTestCaseService;
import com.robomatic.core.v1.services.R2StorageService;
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

    @Autowired
    private R2StorageService r2StorageService;

    @Override
    public TestCaseEntity createTestCase(TestEntity test, String testCases, TestCaseEnum testCaseEnum) {
        try {
            TestCaseEntity testCaseEntity = testCaseMapper.createTestCaseEntity(test, testCaseEnum);

            createTestCaseFile(testCaseEntity.getFileDir(), testCases);

            // Subir a Cloudflare R2
            r2StorageService.uploadTestCase(testCaseEntity.getFileDir(), testCases);

            return testCaseRepository.save(testCaseEntity);
        } catch (Exception e) {
            log.error("Error creating test case: {}", e.getMessage(), e);
            throw new InternalErrorException(E500002, String.format("Conflicts: %s", e.getMessage()));
        }
    }

    private void createTestCaseFile(String fileDir, String testCases) {
        try {
            File newFile = new File(fileDir);
            if (newFile.getParentFile() != null) {
                newFile.getParentFile().mkdirs();
            }
            if (!newFile.exists()) {
                newFile.createNewFile();
            }
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(newFile))) {
                byte[] decodedBytes = Base64.getDecoder().decode(testCases);
                writer.write(new String(decodedBytes));
            }
        } catch (Exception e) {
            log.warn("Could not write test case to local file: {}. Continuing with R2 storage: {}", fileDir, e.getMessage());
        }
    }
}

