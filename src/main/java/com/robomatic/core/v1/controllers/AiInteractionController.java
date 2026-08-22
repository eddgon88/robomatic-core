package com.robomatic.core.v1.controllers;

import com.robomatic.core.v1.models.AiInteractionModel;
import com.robomatic.core.v1.services.AiInteractionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/ai-interactions")
@CrossOrigin(origins = "*")
public class AiInteractionController {

    @Autowired
    private AiInteractionService aiInteractionService;

    @PostMapping("/log")
    public ResponseEntity<Void> logInteraction(@RequestBody AiInteractionModel model) {
        aiInteractionService.logInteraction(model);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/execution/{executionId}")
    public ResponseEntity<List<AiInteractionModel>> getInteractions(@PathVariable Integer executionId) {
        return ResponseEntity.ok(aiInteractionService.getInteractionsByExecution(executionId));
    }
}
