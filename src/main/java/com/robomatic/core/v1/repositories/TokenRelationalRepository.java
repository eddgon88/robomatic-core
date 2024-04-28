package com.robomatic.core.v1.repositories;

import com.robomatic.core.v1.entities.TokenRelationalEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TokenRelationalRepository extends JpaRepository<TokenRelationalEntity, Integer> {

    Optional<TokenRelationalEntity> findByToken(String token);

}
