package com.robomatic.core.v1.dtos.scheduler;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SchedulerDto {

    private String baseUrl;
    private Endpoint endpoint;

}
