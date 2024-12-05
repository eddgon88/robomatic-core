package com.robomatic.core.v1.controllers;

import com.robomatic.core.v1.commons.FunctionCaller;
import com.robomatic.core.v1.jms.JmsSender;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.function.UnaryOperator;

@Slf4j
@RestController
@CrossOrigin(origins = {"http://localhost:4200","http://robomatic.cloud","https://robomatic.cloud"})
@RequestMapping("/core/v1/")
public class JmsController {

    @Autowired
    private JmsSender jmsSender;

    @Autowired
    private FunctionCaller functionCaller;

    @PostMapping(path = "/jms/sendqueue/{queue}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> sendQueue(@PathVariable("queue") String queue) {
        String message = "mensaje";
        UnaryOperator<Object> function = req -> jmsSender.sendQueue((String) req, message);
        return functionCaller.callFunction(queue, function, HttpStatus.OK);
    }
}
