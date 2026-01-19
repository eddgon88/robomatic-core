package com.robomatic.core.v1.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TestExecutionModel {

    private String script;
    private String beforeScript;
    private String afterScript;
    private String testCasesFile;
    private Integer threads;
    private String name;
    private String testExecutionId;
    private boolean web;
    private List<CredentialExecutionModel> credentials;

}
