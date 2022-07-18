package com.robomatic.core.v1.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TestExecutionModel {

    private String script;
    private String testCasesFile;
    private Integer threads;
    private String name;
    private String testExecutionId;

}
