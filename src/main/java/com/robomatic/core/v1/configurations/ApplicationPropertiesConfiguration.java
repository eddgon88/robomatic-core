package com.robomatic.core.v1.configurations;

import com.robomatic.core.v1.clients.impl.MailClientImpl;
import com.robomatic.core.v1.dtos.ConstantsDto;
import com.robomatic.core.v1.dtos.QueuesDto;
import com.robomatic.core.v1.dtos.executor.ExecutorDto;
import com.robomatic.core.v1.dtos.filemanager.FileManagerDto;
import com.robomatic.core.v1.dtos.mail.MailDto;
import com.robomatic.core.v1.dtos.scheduler.SchedulerDto;
import com.robomatic.core.v1.entities.UserEntity;
import com.robomatic.core.v1.models.UserModel;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.querydsl.QPageRequest;
import org.springframework.web.context.annotation.RequestScope;

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

    @Bean
    @ConfigurationProperties(prefix = "executor")
    public ExecutorDto executor() {
        return new ExecutorDto();
    }

    @Bean
    @ConfigurationProperties(prefix = "file-manager")
    public FileManagerDto fileManager() {
        return new FileManagerDto();
    }

    @Bean
    @ConfigurationProperties(prefix = "mail")
    public MailDto mail() {
        return new MailDto();
    }

    @Bean
    @RequestScope
    public UserModel requestScopeRequestData() {
        return new UserModel();
    }


}
