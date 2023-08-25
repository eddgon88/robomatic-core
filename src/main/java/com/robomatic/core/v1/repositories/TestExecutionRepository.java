package com.robomatic.core.v1.repositories;

import com.robomatic.core.v1.entities.TestExecutionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TestExecutionRepository extends JpaRepository<TestExecutionEntity, Integer> {

    Optional<TestExecutionEntity> findByTestExecutionId(String testExecutionId);

    List<TestExecutionEntity> findByTestId(Integer testId);

    @Query(value = "SELECT t FROM TestExecutionEntity t WHERE t.testId = :testId AND t.status = 1")
    Optional<TestExecutionEntity> findByTestIdAndRunningStatus(@Param("testId") Integer testId);
}
