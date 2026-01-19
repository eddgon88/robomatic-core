package com.robomatic.core.v1.repositories;

import com.robomatic.core.v1.entities.ScheduleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ScheduleRepository extends JpaRepository<ScheduleEntity, Long> {

    @Query("SELECT s FROM ScheduleEntity s WHERE s.scheduleId = :scheduleId")
    Optional<ScheduleEntity> findByScheduleId(@Param("scheduleId") String scheduleId);

    @Query("SELECT s FROM ScheduleEntity s WHERE s.testId = :testId AND s.status != 3")
    Optional<ScheduleEntity> findByTestId(@Param("testId") Integer testId);

    @Query("SELECT s FROM ScheduleEntity s WHERE s.status != 3 ORDER BY s.nextRunTime ASC")
    List<ScheduleEntity> findAllActive();

    @Query("SELECT s FROM ScheduleEntity s WHERE s.testId = :testId AND s.status != 3")
    List<ScheduleEntity> findAllByTestId(@Param("testId") Integer testId);

    @Query("SELECT COUNT(s) > 0 FROM ScheduleEntity s WHERE s.testId = :testId AND s.status != 3")
    boolean existsByTestId(@Param("testId") Integer testId);
}


