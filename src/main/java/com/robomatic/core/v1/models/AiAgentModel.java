package com.robomatic.core.v1.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiAgentModel {
    private Integer id;
    private String name;

    private String role;
    private String goal;
    private String backstory;
    private String llm;
    private String company;
    private Integer maxIterations;
    private Boolean verbose;
    private Double temperature;
    private Integer folderId;
}

