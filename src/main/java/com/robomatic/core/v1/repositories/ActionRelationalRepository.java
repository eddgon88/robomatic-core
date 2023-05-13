package com.robomatic.core.v1.repositories;

import com.robomatic.core.v1.entities.ActionEntity;
import com.robomatic.core.v1.entities.ActionRelationalEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ActionRelationalRepository extends JpaRepository<ActionRelationalEntity, Integer> {

    @Query(value = "SELECT a FROM ActionRelationalEntity a WHERE (a.actionId = 1 AND a.userFrom.id = :user) OR (a.actionId in (5, 6, 7) AND a.userTo.id = :user)")
    List<ActionRelationalEntity> findActionsByUser(@Param("user") Integer user);

}
