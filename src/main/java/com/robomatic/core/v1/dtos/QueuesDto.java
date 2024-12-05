package com.robomatic.core.v1.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QueuesDto {

    private String sendToExecute;
    private String insertCaseExecution;
    private String updateTestExecution;
    private String stopTestExecution;
    private String scheduleTestExecution;
    private String parkingLot;

}
