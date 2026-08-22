package com.robomatic.core.v1.services;

import com.robomatic.core.v1.models.AiInteractionModel;
import java.util.List;

public interface AiInteractionService {
    void logInteraction(AiInteractionModel model);
    List<AiInteractionModel> getInteractionsByExecution(Integer testExecutionId);
}
