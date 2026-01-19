package com.robomatic.core.v1.repositories;

import com.robomatic.core.v1.entities.TestCredentialEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TestCredentialRepository extends JpaRepository<TestCredentialEntity, Long> {

    @Query("SELECT tc FROM TestCredentialEntity tc WHERE tc.testId = :testId")
    List<TestCredentialEntity> findByTestId(@Param("testId") Integer testId);

    @Query("SELECT tc FROM TestCredentialEntity tc WHERE tc.testId = :testId AND tc.name = :name")
    Optional<TestCredentialEntity> findByTestIdAndName(@Param("testId") Integer testId, @Param("name") String name);

    @Query("SELECT tc FROM TestCredentialEntity tc WHERE tc.credentialId = :credentialId")
    Optional<TestCredentialEntity> findByCredentialId(@Param("credentialId") String credentialId);

    void deleteByTestId(Integer testId);

}


