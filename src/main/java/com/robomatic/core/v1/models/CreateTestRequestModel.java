package com.robomatic.core.v1.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateTestRequestModel {

    private String name;
    private Integer threads;
    private String script;
    private String testCases;
    private boolean web;
    private Integer folderId;

}
