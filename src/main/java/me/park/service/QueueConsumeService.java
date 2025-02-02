package me.park.service;

import com.solacesystems.jcsmp.JCSMPException;
import me.park.dto.ConsumeQueueRequest;
import me.park.solace.QueueConsumer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class QueueConsumeService {
    private QueueConsumer queueConsumer;

    @Autowired
    public QueueConsumeService(QueueConsumer queueConsumer) {
        this.queueConsumer = queueConsumer;
    }

    public void connectQueue() {
        try {
            queueConsumer.connect();
            System.out.println("QueueConsumer connected successfully to Solace Event Broker.");
        } catch (Exception e) {
            System.out.println("QueueConsumer failed to connect Events Broker err=" + e.getMessage());
        }
    }

    public void consumeQueue(ConsumeQueueRequest coneumReq){
        try {
            queueConsumer.consumeQueue(coneumReq.getQueueName());
        } catch (JCSMPException e) {
            System.out.println("Failed to consume the Queue:" + e.getMessage());
            e.printStackTrace();
        }
    }
}
