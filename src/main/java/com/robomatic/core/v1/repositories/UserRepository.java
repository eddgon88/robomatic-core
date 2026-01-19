package com.robomatic.core.v1.repositories;

import com.robomatic.core.v1.entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Integer> {

    Optional<UserEntity> findByEmail(String email);

    /**
     * Obtiene todos los usuarios que NO son ADMIN (roleId != 1)
     * y que están habilitados
     */
    @Query("SELECT u FROM UserEntity u WHERE u.roleId != :adminRoleId AND u.enabled = true")
    List<UserEntity> findAllExcludingAdmin(@Param("adminRoleId") Integer adminRoleId);

    /**
     * Obtiene todos los usuarios habilitados excepto el usuario actual y los admins
     */
    @Query("SELECT u FROM UserEntity u WHERE u.roleId != :adminRoleId AND u.enabled = true AND u.id != :currentUserId")
    List<UserEntity> findAllExcludingAdminAndCurrentUser(@Param("adminRoleId") Integer adminRoleId, @Param("currentUserId") Integer currentUserId);

}
