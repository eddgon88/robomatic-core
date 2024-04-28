package com.robomatic.core.v1.repositories;

import com.robomatic.core.v1.entities.TokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TokenRepository extends JpaRepository<TokenEntity, Integer> {
}
