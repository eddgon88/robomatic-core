package com.robomatic.core.v1.repositories;

import com.robomatic.core.v1.entities.TestCaseEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface TestCaseRepository extends JpaRepository<TestCaseEntity, Integer> {

    @Query(value = "SELECT tc FROM TestCaseEntity tc WHERE tc.testId = :testId AND tc.typeId = 1")
    Optional<TestCaseEntity> getDefaultByTestId(@Param("testId") Integer testId);

}
