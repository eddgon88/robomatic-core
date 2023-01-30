package com.robomatic.core.v1.dtos.executor;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Endpoint {

    private String executeTest;
    private String stopTestExecution;

}
