package com.robomatic.core.v1.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConstantsDto {

    private String evidenceFileDir;
    private String testPrefix;
    private String testCasePrefix;
    private String testCaseTerminal;
    private String testExecutionPrefix;
    private String caseExecutionPrefix;

}
