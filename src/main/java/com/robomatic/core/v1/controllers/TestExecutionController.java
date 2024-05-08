package com.robomatic.core.v1.controllers;

import com.robomatic.core.v1.commons.FunctionCaller;
import com.robomatic.core.v1.services.TestExecutionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.function.UnaryOperator;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping("/core/v1/test-execution")
@CrossOrigin(origins = "http://localhost:4200")
@Slf4j
public class TestExecutionController {

    @Autowired
    private FunctionCaller functionCaller;

    @Autowired
    private TestExecutionService testExecutionService;

    @GetMapping(path = "/list/{testId}", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> getTestList(@PathVariable("testId") Integer testId) {
        UnaryOperator<Object> function = req -> testExecutionService.getTestExecutionList((Integer) req);
        return functionCaller.callFunction(testId, function, HttpStatus.OK);
    }

    @GetMapping(path = "/ports/{testId}", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> getExecutionPorts(@PathVariable("testId") Integer testId) {
        UnaryOperator<Object> function = req -> testExecutionService.getExecutionPort((Integer) req);
        return functionCaller.callFunction(testId, function, HttpStatus.OK);
    }

}
