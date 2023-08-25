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
public class TestExecutionRecordModel {

    private Integer id;
    private String testExecutionId;
    private Integer testId;
    private String testResultDir;
    private Integer status;
    private LocalDateTime date;
    private String user;

}
