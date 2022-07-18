package com.robomatic.core.v1.services.impl;

import com.robomatic.core.v1.entities.TestCaseEntity;
import com.robomatic.core.v1.exceptions.InternalErrorException;
import com.robomatic.core.v1.exceptions.NotFoundException;
import com.robomatic.core.v1.repositories.TestCaseRepository;
import com.robomatic.core.v1.services.UpdateTestCaseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Base64;

import static com.robomatic.core.v1.exceptions.messages.InternalErrorCode.E500003;
import static com.robomatic.core.v1.exceptions.messages.NotFoundErrorCode.E404001;

@Slf4j
@Service
public class UpdateTestCaseServiceImpl implements UpdateTestCaseService {

    @Autowired
    private TestCaseRepository testCaseRepository;

    @Override
    public TestCaseEntity updateTestCase(Integer testCaseId, String testCases) {

        try {
            TestCaseEntity testCaseEntity = testCaseRepository.findById(testCaseId).orElseThrow(() ->new NotFoundException(E404001));

            updateTestCaseFile(testCaseEntity.getFileDir(), testCases);

            return testCaseEntity;
        } catch (Exception e) {
            throw new InternalErrorException(E500003, String.format("Conflicts: %s", e.getMessage()));
        }

    }

    private void updateTestCaseFile(String fileDir, String testCases) throws IOException {

        BufferedWriter writer = new BufferedWriter(new FileWriter(fileDir));
        byte[] decodedBytes = Base64.getDecoder().decode(testCases);
        writer.write(new String(decodedBytes));
        writer.close();

    }
}
