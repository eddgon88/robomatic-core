package com.robomatic.core.v1.controllers;

import com.robomatic.core.v1.models.UserListModel;
import com.robomatic.core.v1.models.UserModel;
import com.robomatic.core.v1.services.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping("/core/v1/user")
@CrossOrigin(origins = {"http://localhost:4200","http://robomatic.cloud","https://robomatic.cloud"})
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserModel currentUser;

    /**
     * Obtiene la lista de usuarios disponibles para compartir un test específico
     * Excluye:
     * - Al usuario actual
     * - A los administradores
     * - Al owner del test
     * - A usuarios que ya tienen permisos sobre el test
     * 
     * @param testId ID del test a compartir
     */
    @GetMapping(path = "/list/{testId}", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<List<UserListModel>> getUsersForSharing(@PathVariable("testId") Integer testId) {
        log.info("Getting users for sharing test {}", testId);
        List<UserListModel> users = userService.getUsersForSharing(currentUser.getId(), testId);
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    /**
     * Obtiene la lista de usuarios disponibles para compartir un folder específico
     * Excluye:
     * - Al usuario actual
     * - A los administradores
     * - Al owner del folder
     * - A usuarios que ya tienen permisos sobre el folder
     * 
     * @param folderId ID del folder a compartir
     */
    @GetMapping(path = "/list/folder/{folderId}", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<List<UserListModel>> getUsersForSharingFolder(@PathVariable("folderId") Integer folderId) {
        log.info("Getting users for sharing folder {}", folderId);
        List<UserListModel> users = userService.getUsersForSharingFolder(currentUser.getId(), folderId);
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

}

