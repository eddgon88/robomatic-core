package com.robomatic.core.v1.controllers;

import com.robomatic.core.v1.models.CreateScheduleRequestModel;
import com.robomatic.core.v1.models.ScheduleListModel;
import com.robomatic.core.v1.models.ScheduleModel;
import com.robomatic.core.v1.models.UpdateScheduleRequestModel;
import com.robomatic.core.v1.services.ScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping("/core/v1/schedules")
public class ScheduleController {

    @Autowired
    private ScheduleService scheduleService;

    @GetMapping(produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<ScheduleListModel> getAllSchedules() {
        return ResponseEntity.ok(scheduleService.getAllSchedules());
    }

    @GetMapping(path = "/{scheduleId}", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<ScheduleModel> getScheduleById(@PathVariable("scheduleId") String scheduleId) {
        return ResponseEntity.ok(scheduleService.getScheduleById(scheduleId));
    }

    @GetMapping(path = "/test/{testId}", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<ScheduleModel> getScheduleByTestId(@PathVariable("testId") Integer testId) {
        ScheduleModel schedule = scheduleService.getScheduleByTestId(testId);
        if (schedule == null) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(schedule);
    }

    @PostMapping(consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<ScheduleModel> createSchedule(@RequestBody CreateScheduleRequestModel request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(scheduleService.createSchedule(request));
    }

    @PutMapping(path = "/{scheduleId}", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<ScheduleModel> updateSchedule(
            @PathVariable("scheduleId") String scheduleId,
            @RequestBody UpdateScheduleRequestModel request) {
        return ResponseEntity.ok(scheduleService.updateSchedule(scheduleId, request));
    }

    @DeleteMapping(path = "/{scheduleId}")
    public ResponseEntity<Void> deleteSchedule(@PathVariable("scheduleId") String scheduleId) {
        scheduleService.deleteSchedule(scheduleId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping(path = "/{scheduleId}/pause", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<ScheduleModel> pauseSchedule(@PathVariable("scheduleId") String scheduleId) {
        return ResponseEntity.ok(scheduleService.pauseSchedule(scheduleId));
    }

    @PostMapping(path = "/{scheduleId}/resume", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<ScheduleModel> resumeSchedule(@PathVariable("scheduleId") String scheduleId) {
        return ResponseEntity.ok(scheduleService.resumeSchedule(scheduleId));
    }
}

