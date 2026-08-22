package com.robomatic.core.v1.services;

import com.robomatic.core.v1.models.EvidenceModel;
import com.robomatic.core.v1.models.EvidenceNameModel;

import java.util.List;

public interface EvidenceService {

    List<EvidenceModel> getEvidenceList(String testExecutionId);

    List<EvidenceNameModel> getEvidenceNames(String testExecutionId);

    EvidenceModel getEvidenceFile(String testExecutionId, String fileName);

    byte[] downloadAllEvidences(String testExecutionId);

}
