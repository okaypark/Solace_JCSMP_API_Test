package me.park.service;

import com.solacesystems.jcsmp.DeliveryMode;
import me.park.solace.QueueProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class QueueProduceService {
    private QueueProducer queueProducer;

    @Autowired
    public QueueProduceService(QueueProducer queueProducer) {
        this.queueProducer = queueProducer;
    }

    public void connect() {
        try {
            queueProducer.connect();
            System.out.println("QueueProducer connected successfully to Solace Event Broker.");
        } catch (Exception e) {
            System.out.println("Queue Producer failed to connect Events Broker err=" + e.getMessage());
        }
    }

    //큐 메세지 생산
    public void produceQueue(String queueName, DeliveryMode deliveryMode, String message, int count) {
        queueProducer.produceQueue(queueName, deliveryMode, message, count);
    }

    // 토픽 큐 맵핑 produce
    public void produceTopicToQueueMapping(String topic, String queueName, DeliveryMode deliveryMode, String message, int count) {
        queueProducer.produceTopicToQueueMapping(topic, queueName, deliveryMode, message, count);
    }



}
