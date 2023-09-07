package com.robomatic.core.v1.services;

import com.robomatic.core.v1.models.EvidenceModel;

import java.util.List;

public interface EvidenceService {

    List<EvidenceModel> getEvidenceList(String testExecutionId);

}
