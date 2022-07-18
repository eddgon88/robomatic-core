package com.robomatic.core.v1.jms;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.slf4j.Logger;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class JmsSender {

    private final Gson gson = new GsonBuilder()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .create();

    private static final Logger log = org.slf4j.LoggerFactory.getLogger(JmsSender.class);
    @Autowired
    private RabbitTemplate rabbitTemplate;

    public Boolean sendQueue(String queue, Object object) {
        String message = gson.toJson(object);
        log.info("Sending queue");
        this.rabbitTemplate.convertAndSend(queue, message);
        log.info("Queue sent");
        return true;
    }

}
