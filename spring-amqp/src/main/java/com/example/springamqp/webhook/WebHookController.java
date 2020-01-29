package com.example.springamqp.webhook;

import com.example.springamqp.amqp.Producer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
public class WebHookController {

    @Autowired
    Producer producer;

    @PostMapping(value = "/hook", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    @ResponseBody
    public String hook(@RequestBody String pBody) {
        producer.produce(pBody);
        return "OK";
    }
}
