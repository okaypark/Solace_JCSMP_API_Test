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
            System.out.println("Queue Producer connected successfully to Solace Event Broker.");
        } catch (Exception e) {
            System.out.println("Queue Producer failed to connect Events Broker err=" + e.getMessage());
        }
    }

    //큐 메세지 생산
    public void produceQueue(String queueName, DeliveryMode deliveryMode, String message, int count) {
        if (queueName == null || queueName.isEmpty()) {
            throw new IllegalArgumentException("Queue name cannot be null or empty.");
        }
        queueProducer.produceQueue(queueName, deliveryMode, message, count);
    }

    // 토픽 큐 맵핑 produce
    public void produceTopicToQueueMapping(String topic, String queueName, DeliveryMode deliveryMode, String message, int count) {
        if (queueName == null || queueName.isEmpty()) {
            throw new IllegalArgumentException("Queue name cannot be null or empty.");
        }else if (topic == null || topic.isEmpty()) {
            throw new IllegalArgumentException("Topic name cannot be null or empty.");
        }
        queueProducer.produceTopicToQueueMapping(topic, queueName, deliveryMode, message, count);
    }



}
