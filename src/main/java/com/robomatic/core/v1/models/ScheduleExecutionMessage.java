package com.robomatic.core.v1.models;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ScheduleExecutionMessage {
    @SerializedName("schedule_id")
    private String scheduleId;
    
    @SerializedName("test_id")
    private Integer testId;
}
