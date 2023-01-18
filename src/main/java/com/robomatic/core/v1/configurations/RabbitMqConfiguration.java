package com.robomatic.core.v1.configurations;

import com.robomatic.core.v1.dtos.QueuesDto;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class RabbitMqConfiguration {

    @Autowired
    private QueuesDto queuesDto;

    @Value("${spring.rabbitmq.broker-host}")
    private String brokerHost;

    @Value("${spring.rabbitmq.broker-port}")
    private Integer brokerPort;

    @Value("${spring.rabbitmq.user}")
    private String username;

    @Value("${spring.rabbitmq.password}")
    private String password;

    @Bean
    public ConnectionFactory connectionFactory() {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
        connectionFactory.setHost(brokerHost);
        connectionFactory.setPort(brokerPort);
        connectionFactory.setUsername(username);
        connectionFactory.setPassword(password);
        return connectionFactory;
    }

    @Bean
    public RabbitAdmin admin(ConnectionFactory connectionFactory) {
        return new RabbitAdmin(connectionFactory);
    }

    @Bean
    public Queue sendToExecuteQueue() {
        return new Queue(queuesDto.getSendToExecute());
    }

    @Bean
    public Queue insertCaseExecutionQueue() {
        return new Queue(queuesDto.getInsertCaseExecution());
    }

    @Bean
    public Queue updateTestExecutionQueue() {
        return new Queue(queuesDto.getUpdateTestExecution());
    }

    @Bean
    public Queue stopTestExecutionQueue() {
        return new Queue(queuesDto.getStopTestExecution());
    }

    @Bean
    public Queue parkingLotQueue() {
        return new Queue(queuesDto.getParkingLot());
    }

}
