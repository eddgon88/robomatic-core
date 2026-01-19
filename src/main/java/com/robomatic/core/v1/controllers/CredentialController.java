package com.robomatic.core.v1.controllers;

import com.robomatic.core.v1.commons.FunctionCaller;
import com.robomatic.core.v1.models.CreateCredentialRequestModel;
import com.robomatic.core.v1.models.UpdateCredentialRequestModel;
import com.robomatic.core.v1.services.TestCredentialService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.function.UnaryOperator;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping("/core/v1/credential")
@CrossOrigin(origins = {"http://localhost:4200", "http://robomatic.cloud", "https://robomatic.cloud"})
@Slf4j
public class CredentialController {

    @Autowired
    private TestCredentialService credentialService;

    @Autowired
    private FunctionCaller functionCaller;

    @PostMapping(path = "/create", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> createCredential(@RequestBody CreateCredentialRequestModel request) {
        UnaryOperator<Object> function = req -> credentialService.createCredential((CreateCredentialRequestModel) req);
        return functionCaller.callFunction(request, function, HttpStatus.CREATED);
    }

    @PutMapping(path = "/update", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> updateCredential(@RequestBody UpdateCredentialRequestModel request) {
        UnaryOperator<Object> function = req -> credentialService.updateCredential((UpdateCredentialRequestModel) req);
        return functionCaller.callFunction(request, function, HttpStatus.OK);
    }

    @GetMapping(path = "/test/{testId}", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> getCredentialsByTestId(@PathVariable("testId") Integer testId) {
        UnaryOperator<Object> function = req -> credentialService.getCredentialsByTestId((Integer) req);
        return functionCaller.callFunction(testId, function, HttpStatus.OK);
    }

    @DeleteMapping(path = "/{credentialId}", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> deleteCredential(@PathVariable("credentialId") Long credentialId) {
        credentialService.deleteCredential(credentialId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}


