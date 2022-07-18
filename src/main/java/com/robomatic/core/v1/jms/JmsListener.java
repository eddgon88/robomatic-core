package com.robomatic.core.v1.jms;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.robomatic.core.v1.dtos.QueuesDto;
import com.robomatic.core.v1.exceptions.InternalErrorException;
import com.robomatic.core.v1.models.CreateCaseExecutionRequestModel;
import com.robomatic.core.v1.models.UpdateTestExecutionRequestModel;
import com.robomatic.core.v1.services.CreateCaseExecutionService;
import com.robomatic.core.v1.services.UpdateTestExecutionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

import static com.robomatic.core.v1.exceptions.messages.InternalErrorCode.E500001;

@Slf4j
@Component
public class JmsListener {

    protected static final String REDELIVERED_MESSAGE = "redelivered message";

    private final Gson gson = new GsonBuilder()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .create();

    @Autowired
    private CreateCaseExecutionService createCaseExecutionService;

    @Autowired
    private UpdateTestExecutionService updateTestExecutionService;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private QueuesDto queuesDto;

    @RabbitListener(queues = "${queues.insertCaseExecution}")
    @RabbitHandler
    public void createCaseExecution(Message message) {
        log.info("Listening a message from activeMQ: {}", message);
        try {
            String body = validMessage(message);
            if(body.equals(REDELIVERED_MESSAGE)) {
                this.rabbitTemplate.send(queuesDto.getParkingLot(), message);
                return;
            }
            createCaseExecutionService.createCaseExecution(this.gson.fromJson(body, CreateCaseExecutionRequestModel.class));
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new InternalErrorException(E500001);
        }
    }

    @RabbitListener(queues = "${queues.updateTestExecution}")
    @RabbitHandler
    public void updateTestExecution(Message message) {
        log.info("Listening a message from activeMQ: {}", message);
        try {
            String body = validMessage(message);
            if(body.equals(REDELIVERED_MESSAGE)) {
                this.rabbitTemplate.send(queuesDto.getParkingLot(), message);
                return;
            }
            updateTestExecutionService.updateCaseExecution(this.gson.fromJson(body, UpdateTestExecutionRequestModel.class));
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new InternalErrorException(E500001);
        }
    }

    private String validMessage(Message message) {
        Boolean b = message.getMessageProperties().isRedelivered();
        if (Boolean.TRUE.equals(b)) return REDELIVERED_MESSAGE;
        return new String(message.getBody(), StandardCharsets.UTF_8);
    }

}
