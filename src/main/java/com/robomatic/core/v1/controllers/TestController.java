package com.robomatic.core.v1.controllers;

import com.robomatic.core.v1.commons.FunctionCaller;
import com.robomatic.core.v1.exceptions.InternalErrorException;
import com.robomatic.core.v1.exceptions.messages.InternalErrorCode;
import com.robomatic.core.v1.models.CreateTestRequestModel;
import com.robomatic.core.v1.models.TestModel;
import com.robomatic.core.v1.models.UpdateTestRequestModel;
import com.robomatic.core.v1.services.CreateTestService;
import com.robomatic.core.v1.services.DeleteTestService;
import com.robomatic.core.v1.services.ExecuteTestService;
import com.robomatic.core.v1.services.GetTestService;
import com.robomatic.core.v1.services.StopTestExecutionService;
import com.robomatic.core.v1.services.UpdateTestService;
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

import java.io.IOException;
import java.util.function.UnaryOperator;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping("/core/v1/test")
@CrossOrigin(origins = "http://localhost:4200")
@Slf4j
public class TestController {

    @Autowired
    private CreateTestService createTestService;

    @Autowired
    private ExecuteTestService executeTestService;

    @Autowired
    private GetTestService getTestService;

    @Autowired
    private UpdateTestService updateTestService;

    @Autowired
    private StopTestExecutionService stopTestExecutionService;

    @Autowired
    private DeleteTestService deleteTestService;

    @Autowired
    private FunctionCaller functionCaller;

    @PostMapping(path = "/create", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> createTest(@RequestBody(required = true) CreateTestRequestModel createTestRequestModel) {
        UnaryOperator<Object> function = req -> createTestService.createTest((CreateTestRequestModel) req);
        return functionCaller.callFunction(createTestRequestModel, function, HttpStatus.CREATED);
    }

    @PostMapping(path = "/update", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> updateTest(@RequestBody(required = true) UpdateTestRequestModel updateTestRequestModel) {
        UnaryOperator<Object> function = req -> updateTestService.updateTest((UpdateTestRequestModel) req);
        return functionCaller.callFunction(updateTestRequestModel, function, HttpStatus.OK);
    }

    @PostMapping(path = "/execute/{testId}", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> executeTest(@PathVariable("testId") Integer testId) {
        UnaryOperator<Object> function = req -> executeTestService.executeDefaultTest((Integer) req);
        return functionCaller.callFunction(testId, function, HttpStatus.OK);
    }

    @GetMapping(path = "/list/{userId}/{folderId}", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> getTestList(@PathVariable("userId") Integer userId, @PathVariable("folderId") Integer folderId) {
        UnaryOperator<Object> function = req -> getTestService.getTests((Integer) req, folderId);
        return functionCaller.callFunction(userId, function, HttpStatus.OK);
    }

    @PostMapping(path = "/stop/{testId}", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> stopTestExecution(@PathVariable("testId") Integer testId) {
        UnaryOperator<Object> function = req -> stopTestExecutionService.stopTestExecution((Integer) req);
        return functionCaller.callFunction(testId, function, HttpStatus.OK);
    }

    @GetMapping(path = "/{testId}", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> getTest(@PathVariable("testId") Integer testId) {
        TestModel res = new TestModel();
        try {
            res = getTestService.getTest(testId);
        } catch (IOException e) {
            throw new InternalErrorException(InternalErrorCode.E500000);
        }
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @PostMapping(path = "/delete/{testId}", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> deleteTest(@PathVariable("testId") Integer testId) {
        UnaryOperator<Object> function = req -> deleteTestService.deleteTest((Integer) req);
        return functionCaller.callFunction(testId, function, HttpStatus.OK);
    }
}
