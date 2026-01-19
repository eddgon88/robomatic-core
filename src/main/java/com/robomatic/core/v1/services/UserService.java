package com.robomatic.core.v1.services;

import com.robomatic.core.v1.models.UserListModel;

import java.util.List;

public interface UserService {

    /**
     * Obtiene la lista de usuarios disponibles para compartir un test específico
     * Excluye:
     * - Al usuario actual
     * - A los administradores
     * - Al owner del test
     * - A usuarios que ya tienen permisos sobre el test
     * 
     * @param currentUserId ID del usuario actual
     * @param testId ID del test a compartir
     * @return Lista de usuarios disponibles para compartir
     */
    List<UserListModel> getUsersForSharing(Integer currentUserId, Integer testId);

    /**
     * Obtiene la lista de usuarios disponibles para compartir un folder específico
     * Excluye:
     * - Al usuario actual
     * - A los administradores
     * - Al owner del folder
     * - A usuarios que ya tienen permisos sobre el folder
     * 
     * @param currentUserId ID del usuario actual
     * @param folderId ID del folder a compartir
     * @return Lista de usuarios disponibles para compartir
     */
    List<UserListModel> getUsersForSharingFolder(Integer currentUserId, Integer folderId);

}

