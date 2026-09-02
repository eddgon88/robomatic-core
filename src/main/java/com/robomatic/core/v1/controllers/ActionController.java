package com.robomatic.core.v1.controllers;

import com.robomatic.core.v1.commons.FunctionCaller;
import com.robomatic.core.v1.models.PermissionModel;
import com.robomatic.core.v1.models.ShareTestRequest;
import com.robomatic.core.v1.models.UserModel;
import com.robomatic.core.v1.services.ActionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.function.UnaryOperator;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping("/core/v1/action")
@Slf4j
public class ActionController {

    @Autowired
    private ActionService actionService;

    @Autowired
    private FunctionCaller functionCaller;

    @Autowired
    private UserModel currentUser;

    /**
     * Endpoint para compartir un test con otro usuario
     * Solo disponible para owners del test
     */
    @PostMapping(path = "/share", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> shareTest(@RequestBody ShareTestRequest shareTestRequest) {
        log.info("Sharing test {} with user {}", shareTestRequest.getTestId(), shareTestRequest.getUserToId());
        UnaryOperator<Object> function = req -> actionService.shareTest((ShareTestRequest) req, currentUser.getId());
        return functionCaller.callFunction(shareTestRequest, function, HttpStatus.CREATED);
    }

    @GetMapping(path = "/permissions/test/{testId}", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<List<PermissionModel>> getTestPermissions(@PathVariable("testId") Integer testId) {
        log.info("Getting permissions for test {}", testId);
        return new ResponseEntity<>(actionService.getTestPermissions(testId), HttpStatus.OK);
    }

    @GetMapping(path = "/permissions/folder/{folderId}", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<List<PermissionModel>> getFolderPermissions(@PathVariable("folderId") Integer folderId) {
        log.info("Getting permissions for folder {}", folderId);
        return new ResponseEntity<>(actionService.getFolderPermissions(folderId), HttpStatus.OK);
    }

    @DeleteMapping(path = "/permission/{actionId}")
    public ResponseEntity<Void> revokePermission(@PathVariable("actionId") Integer actionId) {
        log.info("Revoking permission action {}", actionId);
        actionService.revokePermission(actionId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}

