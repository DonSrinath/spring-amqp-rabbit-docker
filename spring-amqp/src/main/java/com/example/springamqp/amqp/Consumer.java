package com.example.springamqp.amqp;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class Consumer {
    @Autowired
    ConnectionFactory connectionFactory;

    @Autowired
    Queue queue;

    @Autowired
    ThreadPooledMessageListener threadPooledMessageListener;

    @PostConstruct
    public void init() {
        //TODO: REF https://stackoverflow.com/questions/40848773/spring-handling-rabbitmq-messages-concurrently
        //CURRENTLY ON MESSAGE BEING CALLED FOR MSGS IN THE QUEUE IS PROCESSOR IS BLOCKED ; WAITING
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer(connectionFactory);
        container.setQueues(queue);
        container.setMessageListener(threadPooledMessageListener);
        container.start();

    }
}

@Service
class ThreadPooledMessageListener implements MessageListener {
    @Autowired
    TaskExecutor threadPoolTaskExecutor;

    AtomicInteger processedCount = new AtomicInteger();

    @Override
    public void onMessage(Message message) {
        System.out.println("processed<<" + processedCount.incrementAndGet());
        threadPoolTaskExecutor.execute(new MessageProcessor(message));

    }
}

class MessageProcessor implements Runnable {
    Message processingMessage;

    public MessageProcessor(Message message) {
        this.processingMessage = message;
    }

    @Override
    public void run() {
        System.out.println("================================" + Thread.currentThread().getName());
        System.out.println(processingMessage);
        System.out.println("================================");
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}