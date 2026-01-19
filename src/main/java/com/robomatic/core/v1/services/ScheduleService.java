package com.robomatic.core.v1.services;

import com.robomatic.core.v1.models.CreateScheduleRequestModel;
import com.robomatic.core.v1.models.ScheduleListModel;
import com.robomatic.core.v1.models.ScheduleModel;
import com.robomatic.core.v1.models.UpdateScheduleRequestModel;

public interface ScheduleService {

    ScheduleListModel getAllSchedules();

    ScheduleModel getScheduleById(String scheduleId);

    ScheduleModel getScheduleByTestId(Integer testId);

    ScheduleModel createSchedule(CreateScheduleRequestModel request);

    ScheduleModel updateSchedule(String scheduleId, UpdateScheduleRequestModel request);

    void deleteSchedule(String scheduleId);

    ScheduleModel pauseSchedule(String scheduleId);

    ScheduleModel resumeSchedule(String scheduleId);
}


