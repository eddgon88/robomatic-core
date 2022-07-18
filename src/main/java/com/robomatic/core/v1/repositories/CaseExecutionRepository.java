package com.robomatic.core.v1.repositories;

import com.robomatic.core.v1.entities.CaseExecutionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CaseExecutionRepository extends JpaRepository<CaseExecutionEntity, Integer> {
}
