package com.robomatic.core.v1.services.impl;

import com.robomatic.core.v1.entities.TestEntity;
import com.robomatic.core.v1.exceptions.NotFoundException;
import com.robomatic.core.v1.exceptions.messages.NotFoundErrorCode;
import com.robomatic.core.v1.repositories.TestRepository;
import com.robomatic.core.v1.services.DeleteTestService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class DeleteTestServiceImpl implements DeleteTestService {

    @Autowired
    private TestRepository testRepository;

    @Override
    public TestEntity deleteTest(Integer testId) {
        TestEntity test = testRepository.findById(testId)
                .orElseThrow(() -> new NotFoundException(NotFoundErrorCode.E404002));
        testRepository.delete(test);

        return test;
    }
}
