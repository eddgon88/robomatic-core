package com.robomatic.core.v1.repositories;

import com.robomatic.core.v1.entities.ExecutionCountEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ExecutionCountRepository extends JpaRepository<ExecutionCountEntity, Integer> {
    Optional<ExecutionCountEntity> findByTestIdAndYearAndMonth(Integer testId, Integer year, Integer month);
}
