package com.robomatic.core.v1.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecordModel {

    private Integer id;
    private String recordId;
    private String name;
    private String user;
    private Integer folderId;
    private String type;
    private String permissions;
    private LocalDateTime lastUpdate;
    private LocalDateTime lastExecution;
    private String lastExecutionState;
    private Boolean isRunning;
    private Boolean web;

}
