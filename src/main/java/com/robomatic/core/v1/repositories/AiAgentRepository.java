package com.robomatic.core.v1.repositories;

import com.robomatic.core.v1.entities.AiAgentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AiAgentRepository extends JpaRepository<AiAgentEntity, Integer> {
    
    @Query(value = "SELECT a.* FROM core.ai_agent a " +
                   "JOIN core.test_ai_agent taa ON a.id = taa.ai_agent_id " +
                   "WHERE taa.test_id = :testId", nativeQuery = true)
    List<AiAgentEntity> findByTestId(@Param("testId") Integer testId);

    List<AiAgentEntity> findByFolderId(Integer folderId);

    List<AiAgentEntity> findByUserId(Integer userId);
}

