package com.robomatic.core.v1.dtos.executor;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExecutorDto {

    private String baseUrl;
    private Endpoint endpoint;
}
