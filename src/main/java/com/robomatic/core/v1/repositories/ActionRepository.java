package com.robomatic.core.v1.repositories;

import com.robomatic.core.v1.entities.ActionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ActionRepository extends JpaRepository<ActionEntity, Integer> {

    @Query(value = "SELECT a FROM ActionEntity a WHERE (a.actionId = 1 AND a.userFrom = :user) OR (a.actionId in (5, 6, 7) AND a.userTo = :user)")
    List<ActionEntity> findActionsByUser(@Param("user") Integer user);

    @Query(value = "SELECT * FROM core.action a WHERE test_id = :testId AND a.action_id = 4 ORDER BY date DESC LIMIT 1", nativeQuery=true)
    Optional<ActionEntity> findLastExecutionActionByTestId(@Param("testId") Integer testId);

    @Query(value = "SELECT a FROM ActionEntity a WHERE a.actionId = 4 AND a.testExecutionId = :testExecution")
    Optional<ActionEntity> findExecutionActionByTestExecution(@Param("testExecution") Integer testExecution);

    /**
     * Encuentra al owner (creador) de un test
     * El owner es quien tiene actionId = 1 (CREATE) con userFrom para ese testId
     */
    @Query(value = "SELECT a FROM ActionEntity a WHERE a.testId = :testId AND a.actionId = 1")
    Optional<ActionEntity> findTestOwner(@Param("testId") Integer testId);

    /**
     * Obtiene los IDs de usuarios que ya tienen permisos sobre un test específico
     * Permisos son: actionId in (5, 6, 7) = EXECUTE_PERMISSION, VIEW_PERMISSION, EDIT_PERMISSION
     */
    @Query(value = "SELECT DISTINCT a.userTo FROM ActionEntity a WHERE a.testId = :testId AND a.actionId IN (5, 6, 7)")
    List<Integer> findUsersWithPermissionsOnTest(@Param("testId") Integer testId);

    /**
     * Encuentra al owner (creador) de un folder
     * El owner es quien tiene actionId = 1 (CREATE) con userFrom para ese folderId
     */
    @Query(value = "SELECT a FROM ActionEntity a WHERE a.folderId = :folderId AND a.actionId = 1")
    Optional<ActionEntity> findFolderOwner(@Param("folderId") Integer folderId);

    /**
     * Obtiene los IDs de usuarios que ya tienen permisos sobre un folder específico
     * Permisos son: actionId in (5, 6, 7) = EXECUTE_PERMISSION, VIEW_PERMISSION, EDIT_PERMISSION
     */
    @Query(value = "SELECT DISTINCT a.userTo FROM ActionEntity a WHERE a.folderId = :folderId AND a.actionId IN (5, 6, 7)")
    List<Integer> findUsersWithPermissionsOnFolder(@Param("folderId") Integer folderId);

    /**
     * Verifica si un usuario puede editar un test
     * Puede editar si:
     * - Es el owner (actionId = 1 y userFrom = userId)
     * - Tiene permiso de edición compartido (actionId = 7 y userTo = userId)
     */
    @Query(value = "SELECT COUNT(a) > 0 FROM ActionEntity a WHERE a.testId = :testId AND " +
            "((a.actionId = 1 AND a.userFrom = :userId) OR (a.actionId = 7 AND a.userTo = :userId))")
    boolean canUserEditTest(@Param("testId") Integer testId, @Param("userId") Integer userId);

    /**
     * Verifica si un usuario tiene solo permiso de ejecución sobre un test (no owner, no edit)
     * Permiso de ejecución = actionId = 5
     * 
     * Un usuario tiene "solo permiso de ejecución" si:
     * - Tiene actionId = 5 (EXECUTE_PERMISSION) y userTo = userId
     * - NO es el owner (actionId = 1)
     * - NO tiene permiso de edición (actionId = 7)
     */
    @Query(value = "SELECT COUNT(a) > 0 FROM ActionEntity a WHERE a.testId = :testId AND " +
            "a.actionId = 5 AND a.userTo = :userId")
    boolean hasExecutePermission(@Param("testId") Integer testId, @Param("userId") Integer userId);

    /**
     * Verifica si un usuario puede modificar el test (ejecución o edición)
     * Puede modificar si:
     * - Es el owner (actionId = 1)
     * - Tiene permiso de edición (actionId = 7)
     * - Tiene permiso de ejecución (actionId = 5)
     */
    @Query(value = "SELECT COUNT(a) > 0 FROM ActionEntity a WHERE a.testId = :testId AND " +
            "((a.actionId = 1 AND a.userFrom = :userId) OR (a.actionId IN (5, 7) AND a.userTo = :userId))")
    boolean canUserModifyTest(@Param("testId") Integer testId, @Param("userId") Integer userId);

}
