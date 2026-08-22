package com.robomatic.core.v1.repositories;

import com.robomatic.core.v1.entities.AiInteractionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AiInteractionRepository extends JpaRepository<AiInteractionEntity, Integer> {
    List<AiInteractionEntity> findByTestExecutionIdOrderByTimestampAsc(Integer testExecutionId);
}
