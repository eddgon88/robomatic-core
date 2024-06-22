package com.robomatic.core.v1.services;

import com.robomatic.core.v1.models.RecordModel;
import com.robomatic.core.v1.models.TestModel;

import java.io.IOException;
import java.util.List;

public interface GetTestService {

    List<RecordModel> getTests(Integer folderId);

    TestModel getTest(Integer testId) throws IOException;

}
