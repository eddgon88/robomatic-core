package com.robomatic.core.v1.repositories;

import com.robomatic.core.v1.entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserEntity, Integer> {
}
