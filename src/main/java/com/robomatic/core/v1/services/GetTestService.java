package com.robomatic.core.v1.services;

import com.robomatic.core.v1.models.RecordModel;
import com.robomatic.core.v1.models.TestModel;

import java.io.IOException;
import java.util.List;

public interface GetTestService {

    List<RecordModel> getTests(Integer folderId);

    TestModel getTest(Integer testId) throws IOException;

    /**
     * Obtiene los tests a los que el usuario tiene permisos de owner o editor.
     * Usado para el selector del scheduler.
     */
    List<RecordModel> getTestsForScheduler();

}
