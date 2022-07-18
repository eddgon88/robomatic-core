package com.robomatic.core.v1.services;

import com.robomatic.core.v1.entities.TestCaseEntity;

public interface UpdateTestCaseService {

    TestCaseEntity updateTestCase(Integer testCaseId, String testCases);

}
