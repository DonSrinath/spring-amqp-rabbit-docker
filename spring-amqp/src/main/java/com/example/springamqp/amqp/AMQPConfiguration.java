package com.example.springamqp.amqp;

import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.AsyncAmqpTemplate;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.AsyncRabbitTemplate;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
public class AMQPConfiguration {

    @Value("${amqp.host.url}")
    private String hostname;
    @Value("${amqp.host.virtualhost}")
    private String virtualhost;
    @Value("${amqp.host.port}")
    private int port;
    @Value("${amqp.host.username}")
    private String username;
    @Value("${amqp.host.password}")
    private String password;
    @Value("${amqp.host.connection.limit}")
    private int connection_limit;
    @Value("${amqp.host.connection.queue.fb}")
    private String helloWorldQueueName;

    @Bean
    public ConnectionFactory connectionFactory() {
        //ref : https://www.cloudamqp.com/docs/java.html
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory(hostname);
        connectionFactory.setUsername(username);
        connectionFactory.setPassword(password);
        if (port != 0)
            connectionFactory.setPort(port);
        if (virtualhost != null)
            connectionFactory.setVirtualHost(virtualhost);
        connectionFactory.setConnectionLimit(connection_limit);
        connectionFactory.setRequestedHeartBeat(30);
        connectionFactory.setConnectionTimeout(30000);

        return connectionFactory;
    }

    @Bean
    public AmqpAdmin amqpAdmin() {
        return new RabbitAdmin(connectionFactory());
    }

    @Bean
    public AmqpTemplate amqpTemplate() {
        RabbitTemplate template = new RabbitTemplate(connectionFactory());
        //The routing key is set to the name of the queue by the broker for the default exchange.
        template.setRoutingKey(this.helloWorldQueueName);
        //Where we will synchronously receive messages from
        template.setDefaultReceiveQueue(this.helloWorldQueueName);
        template.setReplyTimeout(1000 * 60 * 5);
        template.setReceiveTimeout(1000 * 60 * 5);

        return template;
    }

    @Bean
    // Every queue is bound to the default direct exchange
    public Queue helloWorldQueue() {
        return new Queue(this.helloWorldQueueName);
    }

    @Bean
    public TaskExecutor threadPoolTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(4);
        executor.setQueueCapacity(10);
        executor.setThreadNamePrefix("fb_msg_processing_executor_thread");
        executor.initialize();
        return executor;
    }

}
