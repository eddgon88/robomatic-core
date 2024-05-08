package com.robomatic.core.v1.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExecutionPort {

    private Integer id;
    private String executionId;
    private String seleniumPort;
    private String vncPort;

}
