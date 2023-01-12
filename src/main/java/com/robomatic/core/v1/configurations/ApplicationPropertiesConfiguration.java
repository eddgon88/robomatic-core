package com.robomatic.core.v1.configurations;

import com.robomatic.core.v1.dtos.ConstantsDto;
import com.robomatic.core.v1.dtos.QueuesDto;
import com.robomatic.core.v1.dtos.scheduler.SchedulerDto;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationPropertiesConfiguration {

    @Bean
    @ConfigurationProperties(prefix = "constants")
    public ConstantsDto constants() {
        return new ConstantsDto();
    }

    @Bean
    @ConfigurationProperties(prefix = "queues")
    public QueuesDto queues() {
        return new QueuesDto();
    }

    @Bean
    @ConfigurationProperties(prefix = "scheduler")
    public SchedulerDto scheduler() {
        return new SchedulerDto();
    }

}
