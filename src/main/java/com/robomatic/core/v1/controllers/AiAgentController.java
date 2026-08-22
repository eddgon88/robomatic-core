package com.robomatic.core.v1.controllers;

import com.robomatic.core.v1.commons.FunctionCaller;
import com.robomatic.core.v1.entities.AiAgentFolderEntity;
import com.robomatic.core.v1.models.AiAgentModel;
import com.robomatic.core.v1.models.CreateFolderRequestModel;
import com.robomatic.core.v1.services.AiAgentFolderService;
import com.robomatic.core.v1.services.AiAgentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.function.UnaryOperator;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping("/core/v1/ai/agent")
@CrossOrigin(origins = {"http://localhost:4200","http://robomatic.cloud","https://robomatic.cloud"})
@Slf4j
public class AiAgentController {

    @Autowired
    private AiAgentService aiAgentService;

    @Autowired
    private AiAgentFolderService aiAgentFolderService;

    @Autowired
    private FunctionCaller functionCaller;

    @PostMapping(path = "/create", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> createAgent(@RequestBody AiAgentModel model) {
        UnaryOperator<Object> function = req -> aiAgentService.createAgent((AiAgentModel) req);
        return functionCaller.callFunction(model, function, HttpStatus.CREATED);
    }

    @PostMapping(path = "/update", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> updateAgent(@RequestBody AiAgentModel model) {
        UnaryOperator<Object> function = req -> aiAgentService.updateAgent((AiAgentModel) req);
        return functionCaller.callFunction(model, function, HttpStatus.OK);
    }

    @GetMapping(path = "/list/{folderId}", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> getAgentList(@PathVariable("folderId") Integer folderId) {
        UnaryOperator<Object> function = req -> aiAgentService.getAgentRecords((Integer) req);
        return functionCaller.callFunction(folderId, function, HttpStatus.OK);
    }

    @GetMapping(path = "/available", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> getAvailableAgents() {
        UnaryOperator<Object> function = req -> aiAgentService.getAvailableAgents();
        return functionCaller.callFunction(null, function, HttpStatus.OK);
    }

    @GetMapping(path = "/{id}", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> getAgent(@PathVariable("id") Integer id) {
        UnaryOperator<Object> function = req -> aiAgentService.getAgent((Integer) req);
        return functionCaller.callFunction(id, function, HttpStatus.OK);
    }

    @PostMapping(path = "/delete/{id}", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> deleteAgent(@PathVariable("id") Integer id) {
        aiAgentService.deleteAgent(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping(path = "/folder/create", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> createFolder(@RequestBody CreateFolderRequestModel model) {
        AiAgentFolderEntity folder = aiAgentFolderService.createFolder(model.getName(), model.getFolderId());
        return new ResponseEntity<>(folder, HttpStatus.CREATED);
    }

    @PostMapping(path = "/folder/delete/{id}", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> deleteFolder(@PathVariable("id") Integer id) {
        aiAgentFolderService.deleteFolder(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
