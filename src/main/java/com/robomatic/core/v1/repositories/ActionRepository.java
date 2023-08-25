package com.robomatic.core.v1.repositories;

import com.robomatic.core.v1.entities.ActionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ActionRepository extends JpaRepository<ActionEntity, Integer> {

    @Query(value = "SELECT a FROM ActionEntity a WHERE (a.actionId = 1 AND a.userFrom = :user) OR (a.actionId in (5, 6, 7) AND a.userTo = :user)")
    List<ActionEntity> findActionsByUser(@Param("user") Integer user);

    @Query(value = "SELECT * FROM core.action a WHERE test_id = :testId AND a.action_id = 4 ORDER BY date DESC LIMIT 1", nativeQuery=true)
    Optional<ActionEntity> findLastExecutionActionByTestId(@Param("testId") Integer testId);

    @Query(value = "SELECT a FROM ActionEntity a WHERE a.actionId = 4 AND a.testExecutionId = :testExecution")
    Optional<ActionEntity> findExecutionActionByTestExecution(@Param("testExecution") Integer testExecution);
}
