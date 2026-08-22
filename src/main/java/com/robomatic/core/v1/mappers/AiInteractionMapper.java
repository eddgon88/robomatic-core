package com.robomatic.core.v1.mappers;

import com.robomatic.core.v1.entities.AiInteractionEntity;
import com.robomatic.core.v1.models.AiInteractionModel;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class AiInteractionMapper {

    public AiInteractionEntity toEntity(AiInteractionModel model) {
        if (model == null) return null;
        return AiInteractionEntity.builder()
                .testExecutionId(model.getTestExecutionId())
                .agentName(model.getAgentName())
                .prompt(model.getPrompt())
                .response(model.getResponse())
                .status(model.getStatus())
                .errorMessage(model.getErrorMessage())
                .tokenUsage(model.getTokenUsage())
                .timestamp(model.getTimestamp())
                .build();
    }

    public AiInteractionModel toModel(AiInteractionEntity entity) {
        if (entity == null) return null;
        return AiInteractionModel.builder()
                .id(entity.getId())
                .testExecutionId(entity.getTestExecutionId())
                .agentName(entity.getAgentName())
                .prompt(entity.getPrompt())
                .response(entity.getResponse())
                .status(entity.getStatus())
                .errorMessage(entity.getErrorMessage())
                .tokenUsage(entity.getTokenUsage())
                .timestamp(entity.getTimestamp())
                .build();
    }

    public List<AiInteractionModel> toModelList(List<AiInteractionEntity> entities) {
        if (entities == null) return null;
        return entities.stream().map(this::toModel).collect(Collectors.toList());
    }
}
