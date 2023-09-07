package com.robomatic.core.v1.clients;

import com.robomatic.core.v1.models.EvidenceModel;

import java.util.List;

public interface FileManagerClient {

    List<EvidenceModel> getEvidenceList(String testExecutionId);

}
