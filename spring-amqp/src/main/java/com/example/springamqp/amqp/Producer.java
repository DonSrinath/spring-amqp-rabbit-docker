package com.example.springamqp.amqp;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class Producer {
    @Autowired
    AmqpTemplate amqpTemplate;

    public void produce(Object object) {

        amqpTemplate.convertAndSend(object);


    }
}
