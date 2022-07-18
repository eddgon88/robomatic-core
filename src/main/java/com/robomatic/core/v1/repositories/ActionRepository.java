package com.robomatic.core.v1.repositories;

import com.robomatic.core.v1.entities.ActionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ActionRepository extends JpaRepository<ActionEntity, Integer> {

    @Query(value = "SELECT a FROM ActionEntity a WHERE (a.actionId = 1 AND a.userFrom = :user) OR (a.actionId in (5, 6, 7) AND a.userTo = :user)")
    List<ActionEntity> findActionsByUser(@Param("user") Integer user);
}
