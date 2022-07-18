package com.robomatic.core.v1.repositories;

import com.robomatic.core.v1.entities.TestEntity;
import com.robomatic.core.v1.models.RecordModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TestRepository extends JpaRepository<TestEntity, Integer> {

    /*@Query(value = "SELECT t.id, t.testId, t.name, t.folderId, a.permissions as permissions \n"
            + "(SELECT testId, CASE WHEN ae.actionId = 1 THEN 1 WHEN ae.actionId = 5 THEN 3 WHEN ae.actionId = 6 THEN 4 WHEN ae.actionId = 7 THEN 2 END\n" +
            " permissions FROM ActionEntity ae WHERE (ae.userFrom = :user AND ae.actionId = 1) OR (ae.userTo = :user AND (ae.actionId = 5 OR ae.actionId = 6 OR ae.actionId = 7))) a,\n"
            +" (SELECT te.id, te.testId, te.name, te.folderId FROM TestEntity te) t WHERE a.testId = t.id AND t.folderId = :folderId")
    List<RecordModel> getRecordList(@Param("user") Integer user, @Param("folderId") Integer folderId);*/

    @Query(value = "SELECT t FROM TestEntity t WHERE t.id in (:ids)")
    List<TestEntity> getTestList(@Param("ids") String ids);


}
