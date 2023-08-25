package com.robomatic.core.v1.controllers;

import com.robomatic.core.v1.commons.FunctionCaller;
import com.robomatic.core.v1.models.CreateFolderRequestModel;
import com.robomatic.core.v1.services.FolderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.function.UnaryOperator;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping("/core/v1/folder")
@CrossOrigin(origins = "http://localhost:4200")
@Slf4j
public class FolderController {

    @Autowired
    private FolderService folderService;

    @Autowired
    private FunctionCaller functionCaller;

    @PostMapping(path = "/create", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> createFolder(@RequestBody(required = true) CreateFolderRequestModel createFolderRequestModel) {
        UnaryOperator<Object> function = req -> folderService.createFolder((CreateFolderRequestModel) req);
        return functionCaller.callFunction(createFolderRequestModel, function, HttpStatus.CREATED);
    }

    @PostMapping(path = "/delete/{folderId}", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> deleteFolder(@PathVariable("folderId") Integer folderId) {
        UnaryOperator<Object> function = req -> folderService.deleteFolder((Integer) req);
        return functionCaller.callFunction(folderId, function, HttpStatus.OK);
    }

}
