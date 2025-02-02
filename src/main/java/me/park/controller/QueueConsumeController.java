package me.park.controller;

import me.park.dto.ConsumeQueueRequest;
import me.park.service.QueueConsumeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/queue")
public class QueueConsumeController {

    private QueueConsumeService queueConsumeService;

    @Autowired
    public QueueConsumeController(QueueConsumeService queueConsumeService) {
        this.queueConsumeService = queueConsumeService;
    }

    @PostMapping("/connect/consumer")
    public String connect() {
        try {
            queueConsumeService.connectQueue();
            return "Queue Consumer connected successfully to Solace Event Broker.\"";
        } catch(Exception e) {
            return "Queue Consumer failed to connect to Solace Event Broker. error=" + e.getMessage();
        }
    }

    @PostMapping("/consumeQueue")
    public void consumeQueue(@RequestBody ConsumeQueueRequest body) {
            queueConsumeService.consumeQueue(body);
    }
}
