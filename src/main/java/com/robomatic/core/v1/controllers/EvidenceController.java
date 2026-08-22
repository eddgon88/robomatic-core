package com.robomatic.core.v1.controllers;

import com.robomatic.core.v1.commons.FunctionCaller;
import com.robomatic.core.v1.services.EvidenceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.function.UnaryOperator;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping("/core/v1/evidence")
@CrossOrigin(origins = {"http://localhost:4200","http://robomatic.cloud","https://robomatic.cloud"})
@Slf4j
public class EvidenceController {

    @Autowired
    private FunctionCaller functionCaller;

    @Autowired
    private EvidenceService evidenceService;

    @GetMapping(path = "/{testExecutionId}", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> getEvidenceList(@PathVariable("testExecutionId") String testExecutionId) {
        UnaryOperator<Object> function = req -> evidenceService.getEvidenceList((String) req);
        return functionCaller.callFunction(testExecutionId, function, HttpStatus.OK);
    }

    @GetMapping(path = "/names/{testExecutionId}", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> getEvidenceNames(@PathVariable("testExecutionId") String testExecutionId) {
        UnaryOperator<Object> function = req -> evidenceService.getEvidenceNames((String) req);
        return functionCaller.callFunction(testExecutionId, function, HttpStatus.OK);
    }

    @GetMapping(path = "/{testExecutionId}/file/{fileName}", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> getEvidenceFile(
            @PathVariable("testExecutionId") String testExecutionId,
            @PathVariable("fileName") String fileName) {
        UnaryOperator<Object> function = req -> evidenceService.getEvidenceFile(testExecutionId, (String) req);
        return functionCaller.callFunction(fileName, function, HttpStatus.OK);
    }

    @GetMapping(path = "/{testExecutionId}/download-all", produces = "application/zip")
    public ResponseEntity<byte[]> downloadAllEvidences(@PathVariable("testExecutionId") String testExecutionId) {
        byte[] zipBytes = evidenceService.downloadAllEvidences(testExecutionId);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("application/zip"));
        headers.setContentDispositionFormData("attachment", "evidences_" + testExecutionId + ".zip");
        return new ResponseEntity<>(zipBytes, headers, HttpStatus.OK);
    }
}
