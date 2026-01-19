package com.robomatic.core.v1.services.impl;

import com.robomatic.core.v1.entities.ActionEntity;
import com.robomatic.core.v1.entities.UserEntity;
import com.robomatic.core.v1.enums.RoleEnum;
import com.robomatic.core.v1.models.UserListModel;
import com.robomatic.core.v1.repositories.ActionRepository;
import com.robomatic.core.v1.repositories.UserRepository;
import com.robomatic.core.v1.services.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ActionRepository actionRepository;

    @Override
    public List<UserListModel> getUsersForSharing(Integer currentUserId, Integer testId) {
        log.info("Getting users for sharing test {}, excluding user: {}", testId, currentUserId);
        
        // Obtener usuarios base (excluyendo admin y usuario actual)
        List<UserEntity> users = userRepository.findAllExcludingAdminAndCurrentUser(
            RoleEnum.ADMIN.getCode(), 
            currentUserId
        );

        // Construir conjunto de usuarios a excluir
        Set<Integer> usersToExclude = new HashSet<>();
        
        // Agregar el owner del test a la lista de exclusión
        Optional<ActionEntity> ownerAction = actionRepository.findTestOwner(testId);
        ownerAction.ifPresent(action -> {
            log.info("Test {} owner is user {}", testId, action.getUserFrom());
            usersToExclude.add(action.getUserFrom());
        });
        
        // Agregar usuarios que ya tienen permisos sobre el test
        List<Integer> usersWithPermissions = actionRepository.findUsersWithPermissionsOnTest(testId);
        log.info("Users with existing permissions on test {}: {}", testId, usersWithPermissions);
        usersToExclude.addAll(usersWithPermissions);

        // Filtrar usuarios excluyendo owner y usuarios con permisos existentes
        return users.stream()
                .filter(user -> !usersToExclude.contains(user.getId()))
                .map(this::mapToUserListModel)
                .collect(Collectors.toList());
    }

    @Override
    public List<UserListModel> getUsersForSharingFolder(Integer currentUserId, Integer folderId) {
        log.info("Getting users for sharing folder {}, excluding user: {}", folderId, currentUserId);
        
        // Obtener usuarios base (excluyendo admin y usuario actual)
        List<UserEntity> users = userRepository.findAllExcludingAdminAndCurrentUser(
            RoleEnum.ADMIN.getCode(), 
            currentUserId
        );

        // Construir conjunto de usuarios a excluir
        Set<Integer> usersToExclude = new HashSet<>();
        
        // Agregar el owner del folder a la lista de exclusión
        Optional<ActionEntity> ownerAction = actionRepository.findFolderOwner(folderId);
        ownerAction.ifPresent(action -> {
            log.info("Folder {} owner is user {}", folderId, action.getUserFrom());
            usersToExclude.add(action.getUserFrom());
        });
        
        // Agregar usuarios que ya tienen permisos sobre el folder
        List<Integer> usersWithPermissions = actionRepository.findUsersWithPermissionsOnFolder(folderId);
        log.info("Users with existing permissions on folder {}: {}", folderId, usersWithPermissions);
        usersToExclude.addAll(usersWithPermissions);

        // Filtrar usuarios excluyendo owner y usuarios con permisos existentes
        return users.stream()
                .filter(user -> !usersToExclude.contains(user.getId()))
                .map(this::mapToUserListModel)
                .collect(Collectors.toList());
    }

    private UserListModel mapToUserListModel(UserEntity entity) {
        return UserListModel.builder()
                .id(entity.getId())
                .fullName(entity.getFullName())
                .email(entity.getEmail())
                .build();
    }

}

