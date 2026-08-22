package com.robomatic.core.v1.repositories;

import com.robomatic.core.v1.entities.AiAgentFolderEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AiAgentFolderRepository extends JpaRepository<AiAgentFolderEntity, Integer> {
    List<AiAgentFolderEntity> findByParentId(Integer parentId);
}
