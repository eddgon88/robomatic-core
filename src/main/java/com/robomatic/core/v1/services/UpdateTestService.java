package com.robomatic.core.v1.services;

import com.robomatic.core.v1.entities.TestEntity;
import com.robomatic.core.v1.models.UpdateTestRequestModel;

public interface UpdateTestService {

    TestEntity updateTest(UpdateTestRequestModel updateTestRequest);

}
