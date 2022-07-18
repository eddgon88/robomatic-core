package com.robomatic.core.v1.services;

import com.robomatic.core.v1.entities.TestEntity;
import com.robomatic.core.v1.models.CreateTestRequestModel;


public interface CreateTestService {

    TestEntity createTest(CreateTestRequestModel createTestRequest);

}
