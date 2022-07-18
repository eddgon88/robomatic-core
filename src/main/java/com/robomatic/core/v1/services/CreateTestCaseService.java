package com.robomatic.core.v1.services;

import com.robomatic.core.v1.entities.TestCaseEntity;
import com.robomatic.core.v1.entities.TestEntity;
import com.robomatic.core.v1.enums.TestCaseEnum;

public interface CreateTestCaseService {

    TestCaseEntity createTestCase(TestEntity test, String testCases, TestCaseEnum testCaseEnum);

}
