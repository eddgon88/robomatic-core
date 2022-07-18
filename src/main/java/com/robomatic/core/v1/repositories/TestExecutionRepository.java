package com.robomatic.core.v1.repositories;

import com.robomatic.core.v1.entities.TestExecutionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TestExecutionRepository extends JpaRepository<TestExecutionEntity, Integer> {

    Optional<TestExecutionEntity> findByTestExecutionId(String testExecutionId);
}
