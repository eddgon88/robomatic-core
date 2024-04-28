package com.robomatic.core.v1.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TestModel {

    private Integer id;
    private String testId;
    private String name;
    private Integer threads;
    private String script;
    private String beforeScript;
    private String afterScript;
    private boolean web;
    private Integer folderId;
    private Integer testCaseId;
    private String testCases;
    private String permissions;
    private String description;

}
