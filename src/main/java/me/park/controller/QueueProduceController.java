package me.park.controller;

import com.solacesystems.jcsmp.DeliveryMode;
import me.park.dto.ProduceQueueRequest;
import me.park.dto.ProduceTopicToQueueRequest;
import me.park.service.QueueProduceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("api/queue")
public class QueueProduceController {

    private final QueueProduceService queueProduceService;

    //생성자 주입
    @Autowired
    public QueueProduceController(QueueProduceService queueProduceService) {
        this.queueProduceService = queueProduceService;
    }

    //Event Broker에 연결
    @PostMapping("/connect")
    public String connect() {
        try {
            queueProduceService.connect();
            return "Queue Producer connected successfully to Solace Event Broker.\"";
        } catch(Exception e) {
            return "Queue Producer failed to connect to Solace Event Broker. error=" + e.getMessage();
        }
    }

    @PostMapping("/produceQueue")
    public String produceQueue(@RequestBody ProduceQueueRequest body) {
        try {
            String queueName = body.getQueueName();
            String message = body.getMessage();
            DeliveryMode deliveryMode = body.getDeliveryMode();
            int count = body.getCount();

            queueProduceService.produceQueue(queueName, deliveryMode, message, count);
            return "Queue Producer sent message successfully to Solace Event Broker.";
        } catch(Exception e) {
            return "Queue Producer failed to send error:" + e.getMessage();
        }
    }

    @PostMapping("/produceTopicToQueue")
    public String produce(@RequestBody ProduceTopicToQueueRequest body) {
        try {
            String topic = body.getTopic();
            String queueName = body.getQueueName();
            String message = body.getMessage();
            DeliveryMode deliveryMode = body.getDeliveryMode();
            int count = body.getCount();

            queueProduceService.produceTopicToQueueMapping(topic, queueName, deliveryMode, message, count);
            return "Queue Producer sent message successfully to Solace Event Broker.";
        } catch(Exception e) {
            return "Queue Producer failed to send error:" + e.getMessage();
        }
    }




}
