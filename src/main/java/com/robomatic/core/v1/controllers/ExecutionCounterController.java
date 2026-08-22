package com.robomatic.core.v1.controllers;

import com.robomatic.core.v1.commons.FunctionCaller;
import com.robomatic.core.v1.exceptions.BadRequestException;
import com.robomatic.core.v1.exceptions.ForbiddenException;
import com.robomatic.core.v1.exceptions.messages.BadRequestErrorCode;
import com.robomatic.core.v1.models.UserModel;
import com.robomatic.core.v1.repositories.ActionRepository;
import com.robomatic.core.v1.services.ExecutionCounterService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.function.UnaryOperator;

import static com.robomatic.core.v1.exceptions.messages.ForbiddenErrorCode.E403002;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping("/core/v1/execution-counter")
@CrossOrigin(origins = {"http://localhost:4200", "http://robomatic.cloud", "https://robomatic.cloud"})
@Slf4j
public class ExecutionCounterController {

    @Autowired
    private ExecutionCounterService executionCounterService;

    @Autowired
    private ActionRepository actionRepository;

    @Autowired
    private UserModel currentUser;

    @Autowired
    private FunctionCaller functionCaller;

    @GetMapping(path = "/{testId}", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> getCounterData(
            @PathVariable("testId") Integer testId,
            @RequestParam(value = "year", required = false) Integer year,
            @RequestParam(value = "month", required = false) Integer month) {
        // Anyone with access to the test list can probably see the counts, 
        // but we can restrict it to those who can at least modify/view if needed.
        if (!actionRepository.canUserModifyTest(testId, currentUser.getId()) && !isAdminOrAnalyst()) {
            throw new ForbiddenException(E403002, "You don't have permission to view limits for this test");
        }
        return new ResponseEntity<>(executionCounterService.getCounterData(testId, year, month), HttpStatus.OK);
    }

    @PostMapping(path = "/update", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> updateLimit(@RequestBody Map<String, Object> body) {
        Integer testId = getInt(body, "test_id");
        Integer maxExecutions = getInt(body, "max_executions");
        Integer year = getInt(body, "year");
        Integer month = getInt(body, "month");

        if (testId == null) {
            throw new BadRequestException(BadRequestErrorCode.E400001);
        }

        if (!actionRepository.canUserEditTest(testId, currentUser.getId()) && !isAdminOrAnalyst()) {
            throw new ForbiddenException(E403002, "You don't have permission to configure limits for this test");
        }

        UnaryOperator<Object> function = req -> {
            executionCounterService.updateMaxExecutions(testId, maxExecutions, year, month);
            return Map.of("message", "Limit updated successfully");
        };

        return functionCaller.callFunction(testId, function, HttpStatus.OK);
    }

    private Integer getInt(Map<String, Object> body, String key) {
        Object val = body.get(key);
        if (val == null) return null;
        if (val instanceof Integer) return (Integer) val;
        if (val instanceof Number) return ((Number) val).intValue();
        try {
            return Integer.valueOf(val.toString());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private boolean isAdminOrAnalyst() {
        Integer roleId = currentUser.getRoleId();
        return roleId != null && (roleId == 1 || roleId == 2); // 1: ADMIN, 2: ANALYST
    }
}
