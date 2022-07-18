package com.robomatic.core.v1.repositories;

import com.robomatic.core.v1.entities.FolderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FolderRepository extends JpaRepository<FolderEntity, Integer> {

    @Query(value = "SELECT f FROM FolderEntity f WHERE f.id in (:ids)")
    List<FolderEntity> getFolderList(@Param("ids") String ids);

}
