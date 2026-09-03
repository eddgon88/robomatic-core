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
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.backoff.FixedBackOff;

import java.net.URI;

@Configuration
public class RabbitMqConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(RabbitMqConfiguration.class);

    @Autowired
    private QueuesDto queuesDto;

    @Value("${cloudamqp.url:${CLOUDAMQP_URL:}}")
    private String rabbitmqUrl;

    @Value("${rabbitmq.host:${RABBITMQ_HOST:${spring.rabbitmq.broker-host:localhost}}}")
    private String brokerHost;

    @Value("${rabbitmq.port:${RABBITMQ_PORT:${spring.rabbitmq.broker-port:5672}}}")
    private Integer brokerPort;

    @Value("${rabbitmq.user:${RABBITMQ_USER:${spring.rabbitmq.user:admin}}}")
    private String username;

    @Value("${rabbitmq.password:${RABBITMQ_PASSWORD:${spring.rabbitmq.password:admin}}}")
    private String password;

    @Value("${rabbitmq.vhost:${RABBITMQ_VHOST:/}}")
    private String virtualHost;

    @Value("${rabbitmq.ssl:${RABBITMQ_SSL:false}}")
    private Boolean sslEnabled;

    @Value("${rabbitmq.listener.auto-startup:${RABBITMQ_LISTENER_AUTO_STARTUP:true}}")
    private Boolean listenerAutoStartup;

    @Value("${rabbitmq.listener.max-attempts:${RABBITMQ_LISTENER_MAX_ATTEMPTS:3}}")
    private Long maxListenerRecoveryAttempts;

    @Value("${rabbitmq.listener.recovery-interval:${RABBITMQ_LISTENER_RECOVERY_INTERVAL:10000}}")
    private Long listenerRecoveryInterval;


    @Bean
    public ConnectionFactory connectionFactory() {
        String host = brokerHost;
        int port = brokerPort;
        String user = username;
        String pass = password;
        String vhost = virtualHost;
        boolean useSsl = Boolean.TRUE.equals(sslEnabled);

        if (rabbitmqUrl != null && !rabbitmqUrl.trim().isEmpty()) {
            try {
                String cleanedUri = rabbitmqUrl.trim().replaceAll("^[\"']|[\"']$", "");
                String maskedUri = cleanedUri.replaceAll(":[^:@]+@", ":****@");
                logger.info("Parsing RabbitMQ configuration from URI: {}", maskedUri);

                URI uri = URI.create(cleanedUri);
                if (uri.getHost() != null) host = uri.getHost();
                if (uri.getPort() > 0) {
                    port = uri.getPort();
                } else if ("amqps".equalsIgnoreCase(uri.getScheme())) {
                    port = 5671;
                }
                if (uri.getUserInfo() != null) {
                    String[] credentials = uri.getUserInfo().split(":", 2);
                    user = credentials[0].trim();
                    if (credentials.length > 1) {
                        pass = credentials[1].trim().replaceAll("^[\"']|[\"']$", "");
                    }
                }
                if (uri.getPath() != null && uri.getPath().length() > 1) {
                    vhost = uri.getPath().substring(1).trim().replaceAll("^[\"']|[\"']$", "");
                }
                if ("amqps".equalsIgnoreCase(uri.getScheme())) {
                    useSsl = true;
                }
            } catch (Exception e) {
                logger.error("Error parsing CloudAMQP URI: {}", e.getMessage(), e);
            }
        }

        try {
            logger.info("Initializing RabbitMQ ConnectionFactory -> host: {}, port: {}, vhost: [{}], user: [{}], passLength: {}, ssl: {}",
                    host, port, vhost, user, (pass != null ? pass.length() : 0), useSsl);

            com.rabbitmq.client.ConnectionFactory rabbitFactory = new com.rabbitmq.client.ConnectionFactory();
            rabbitFactory.setHost(host);
            rabbitFactory.setPort(port);
            rabbitFactory.setUsername(user);
            rabbitFactory.setPassword(pass);
            rabbitFactory.setVirtualHost(vhost);

            if (useSsl) {
                rabbitFactory.useSslProtocol();
                rabbitFactory.enableHostnameVerification();
            }

            CachingConnectionFactory connectionFactory = new CachingConnectionFactory(rabbitFactory);
            connectionFactory.setHost(host);
            connectionFactory.setPort(port);
            connectionFactory.setUsername(user);
            connectionFactory.setPassword(pass);
            connectionFactory.setVirtualHost(vhost);

            return connectionFactory;
        } catch (Exception e) {
            logger.error("Failed to initialize RabbitMQ ConnectionFactory: {}", e.getMessage(), e);
            CachingConnectionFactory fallback = new CachingConnectionFactory();
            fallback.setHost(host);
            fallback.setPort(port);
            return fallback;
        }
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        boolean autoStart = listenerAutoStartup == null || Boolean.TRUE.equals(listenerAutoStartup);
        factory.setAutoStartup(autoStart);
        factory.setMissingQueuesFatal(false);
        long attempts = (maxListenerRecoveryAttempts != null && maxListenerRecoveryAttempts > 0) ? maxListenerRecoveryAttempts : 3L;
        long interval = (listenerRecoveryInterval != null && listenerRecoveryInterval > 0) ? listenerRecoveryInterval : 10000L;
        factory.setFailedDeclarationRetryInterval(interval);
        factory.setRecoveryBackOff(new FixedBackOff(interval, attempts));
        logger.info("Configured rabbitListenerContainerFactory -> autoStartup: {}, maxAttempts: {}, recoveryInterval: {}ms",
                autoStart, attempts, interval);
        return factory;
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        return new RabbitTemplate(connectionFactory);
    }

    @Bean
    public RabbitAdmin admin(ConnectionFactory connectionFactory) {
        RabbitAdmin rabbitAdmin = new RabbitAdmin(connectionFactory);
        rabbitAdmin.setIgnoreDeclarationExceptions(true);
        return rabbitAdmin;
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


