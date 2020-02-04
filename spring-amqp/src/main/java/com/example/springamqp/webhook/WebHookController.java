package com.example.springamqp.webhook;

import com.example.springamqp.amqp.Producer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
public class WebHookController {

    @Autowired
    Producer producer;

    @PostMapping(value = "/hook/{source}/{target}", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    @ResponseBody
    public String hook(@PathVariable(value="source", required = true) String source,@PathVariable(value="target" , required = true) String target, @RequestBody String pBody) {
        System.out.println("source:"+source);
        System.out.println("target:"+target);
        producer.produce(pBody);
        //fake OK : must respond after success posting to queue
        //TODO:fix this
        return "OK";
    }
}
