package com.robomatic.core.v1.services;

import com.robomatic.core.v1.models.AiAgentModel;
import com.robomatic.core.v1.models.RecordModel;

import java.util.List;

public interface AiAgentService {
    AiAgentModel createAgent(AiAgentModel model);
    AiAgentModel updateAgent(AiAgentModel model);
    void deleteAgent(Integer id);
    AiAgentModel getAgent(Integer id);
    List<RecordModel> getAgentRecords(Integer folderId);
    List<AiAgentModel> getAgentsForTest(Integer testId);
    List<AiAgentModel> getAvailableAgents();
}
