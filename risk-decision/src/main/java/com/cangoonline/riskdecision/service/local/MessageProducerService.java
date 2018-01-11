package com.cangoonline.riskdecision.service.local;

import com.cangoonline.disruptor.model.Message;
import com.cangoonline.disruptor.producer.MessageProducer;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Created by Administrator on 2017\12\11 0011.
 */
@Component
public class MessageProducerService {
    private MessageProducer producer;

    public void publish(Message message) {
        producer.publish(message);
    }
    public void publish(Message message , Map<String,Object> messageProperties) {
        producer.publish(message,messageProperties);
    }

    public MessageProducer getProducer() {
        return producer;
    }

    public void setProducer(MessageProducer producer) {
        this.producer = producer;
    }
}
