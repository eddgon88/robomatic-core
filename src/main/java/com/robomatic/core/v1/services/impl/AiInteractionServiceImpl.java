package com.robomatic.core.v1.services.impl;

import com.robomatic.core.v1.entities.AiInteractionEntity;
import com.robomatic.core.v1.mappers.AiInteractionMapper;
import com.robomatic.core.v1.models.AiInteractionModel;
import com.robomatic.core.v1.repositories.AiInteractionRepository;
import com.robomatic.core.v1.services.AiInteractionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class AiInteractionServiceImpl implements AiInteractionService {

    @Autowired
    private AiInteractionRepository aiInteractionRepository;

    @Autowired
    private AiInteractionMapper aiInteractionMapper;

    @Override
    public void logInteraction(AiInteractionModel model) {
        try {
            AiInteractionEntity entity = aiInteractionMapper.toEntity(model);
            if (entity.getTimestamp() == null) {
                entity.setTimestamp(LocalDateTime.now());
            }
            aiInteractionRepository.save(entity);
            log.info("Logged AI interaction for execution {}: agent={}, status={}", 
                    model.getTestExecutionId(), model.getAgentName(), model.getStatus());
        } catch (Exception e) {
            log.error("Error logging AI interaction: {}", e.getMessage());
        }
    }

    @Override
    public List<AiInteractionModel> getInteractionsByExecution(Integer testExecutionId) {
        List<AiInteractionEntity> entities = aiInteractionRepository.findByTestExecutionIdOrderByTimestampAsc(testExecutionId);
        return aiInteractionMapper.toModelList(entities);
    }
}
