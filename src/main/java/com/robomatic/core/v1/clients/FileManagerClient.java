package com.robomatic.core.v1.clients;

import com.robomatic.core.v1.models.EvidenceModel;
import com.robomatic.core.v1.models.EvidenceNameModel;

import java.util.List;

public interface FileManagerClient {

    List<EvidenceModel> getEvidenceList(String testExecutionId);

    List<EvidenceNameModel> getEvidenceNames(String testExecutionId);

    EvidenceModel getEvidenceFile(String testExecutionId, String fileName);

    byte[] downloadAllEvidences(String testExecutionId);

}
