package com.robomatic.core.v1.repositories;

import com.robomatic.core.v1.entities.TestExecutionEntity;
import com.robomatic.core.v1.models.TestExecutionRecordModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface TestExecutionRepository extends JpaRepository<TestExecutionEntity, Integer> {

    Optional<TestExecutionEntity> findByTestExecutionId(String testExecutionId);

    List<TestExecutionEntity> findByTestId(Integer testId);

    @Query(value = "SELECT t FROM TestExecutionEntity t WHERE t.testId = :testId AND t.status = 1")
    Optional<TestExecutionEntity> findByTestIdAndRunningStatus(@Param("testId") Integer testId);

    @Query(value = "SELECT DISTINCT t.testId FROM TestExecutionEntity t WHERE t.testId IN :testIds AND t.status = 1")
    Set<Integer> findRunningTestIds(@Param("testIds") Collection<Integer> testIds);

    @Query(value = "SELECT new com.robomatic.core.v1.models.TestExecutionRecordModel(" +
            "t.id, t.testExecutionId, t.testId, t.testResultsDir, t.status, a.date, u.fullName) " +
            "FROM TestExecutionEntity t, ActionEntity a, UserEntity u " +
            "WHERE t.testId = :testId AND a.testExecutionId = t.id AND a.actionId = 4 AND u.id = a.userFrom " +
            "ORDER BY a.date DESC")
    List<TestExecutionRecordModel> findExecutionRecordsByTestId(@Param("testId") Integer testId);
}
