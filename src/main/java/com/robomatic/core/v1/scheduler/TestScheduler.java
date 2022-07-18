/*package com.robomatic.core.v1.scheduler;

import com.robomatic.core.v1.services.ExecuteTestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.stereotype.Component;

@Component
public class TestScheduler {

    @Autowired
    private ExecuteTestService executeTestService;

    private ScheduledTaskRegistrar taskRegistrar;

    @Autowired
    public void setScheduler(ScheduledTaskRegistrar taskRegistrar) {
        this.taskRegistrar = taskRegistrar;
    }

    public void schedule(Integer testId, Integer testCaseId, String cron) {
        taskRegistrar.addCronTask(() -> executeTestService.executeTest(testId, testCaseId), cron);
    }

}
*/