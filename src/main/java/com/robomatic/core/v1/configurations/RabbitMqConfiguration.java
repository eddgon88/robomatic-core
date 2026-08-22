package com.robomatic.core.v1.configurations;

import com.robomatic.core.v1.dtos.QueuesDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.URI;

@Configuration
public class RabbitMqConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(RabbitMqConfiguration.class);

    @Autowired
    private QueuesDto queuesDto;

    @Value("${spring.rabbitmq.addresses:${CLOUDAMQP_URL:}}")
    private String rabbitmqAddresses;

    @Value("${spring.rabbitmq.broker-host:localhost}")
    private String brokerHost;

    @Value("${spring.rabbitmq.broker-port:5672}")
    private Integer brokerPort;

    @Value("${spring.rabbitmq.user:admin}")
    private String username;

    @Value("${spring.rabbitmq.password:admin}")
    private String password;

    @Bean
    public ConnectionFactory connectionFactory() {
        // --- NUEVA CONFIGURACION: CLOUDAMQP SSL URI ---
        if (rabbitmqAddresses != null && !rabbitmqAddresses.trim().isEmpty()) {
            try {
                logger.info("Configuring RabbitMQ ConnectionFactory with CloudAMQP URI: {}", rabbitmqAddresses);
                URI uri = URI.create(rabbitmqAddresses.trim());
                CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
                connectionFactory.setHost(uri.getHost());
                int port = uri.getPort() > 0 ? uri.getPort() : ("amqps".equalsIgnoreCase(uri.getScheme()) ? 5671 : 5672);
                connectionFactory.setPort(port);

                if (uri.getUserInfo() != null) {
                    String[] credentials = uri.getUserInfo().split(":", 2);
                    connectionFactory.setUsername(credentials[0]);
                    if (credentials.length > 1) {
                        connectionFactory.setPassword(credentials[1]);
                    }
                }
                if (uri.getPath() != null && uri.getPath().length() > 1) {
                    connectionFactory.setVirtualHost(uri.getPath().substring(1));
                }
                if ("amqps".equalsIgnoreCase(uri.getScheme())) {
                    connectionFactory.getRabbitConnectionFactory().useSslProtocol();
                }
                logger.info("RabbitMQ successfully configured -> host: {}, port: {}, virtualHost: {}, ssl: {}",
                        connectionFactory.getHost(), connectionFactory.getPort(), connectionFactory.getVirtualHost(), "amqps".equalsIgnoreCase(uri.getScheme()));
                return connectionFactory;
            } catch (Exception e) {
                logger.error("Error setting up CloudAMQP URI ConnectionFactory: {}", e.getMessage(), e);
            }
        }

        // --- CODIGO ORIGINAL EC2 / LOCAL (Fallback) ---
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
        connectionFactory.setHost(brokerHost);
        connectionFactory.setPort(brokerPort);
        connectionFactory.setUsername(username);
        connectionFactory.setPassword(password);
        return connectionFactory;
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setAutoStartup(true);
        return factory;
    }

    @Bean
    public RabbitAdmin admin(ConnectionFactory connectionFactory) {
        return new RabbitAdmin(connectionFactory);
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
    public Queue scheduleTestExecutionQueue() {
        return new Queue(queuesDto.getScheduleTestExecution());
    }

    @Bean
    public Queue incrementExecutionCountQueue() {
        return new Queue(queuesDto.getIncrementExecutionCount());
    }

    @Bean
    public Queue parkingLotQueue() {
        return new Queue(queuesDto.getParkingLot());
    }

}

