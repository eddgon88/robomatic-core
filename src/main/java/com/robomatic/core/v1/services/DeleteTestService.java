package com.robomatic.core.v1.services;

import com.robomatic.core.v1.entities.TestEntity;

public interface DeleteTestService {

    TestEntity deleteTest(Integer testId);

}
