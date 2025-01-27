package me.park.controller;

import me.park.service.PublisherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * PublisherController:
 * 메시지 발행과 관련된 PUB REST API를 제공하는 컨트롤러입니다.
 */
@RestController
@RequestMapping("/api/publisher")
public class PublisherController {

    private final PublisherService publisherService;

    @Autowired
    public PublisherController(PublisherService publisherService) {
        this.publisherService = publisherService;
    }

    //HTTP connect 실행시 Solace Events Broker 연결(현재는 JSCMP Session Connect
    @PostMapping("/connect")
    public String connect() {
        return publisherService.connect();
    }   // Service에 Events Broker Connect처리


    //HTTP에서 Publish 실행시 (Solace Events Broker에 게시)
    @PostMapping("/publish")
    public String publish(@RequestBody Map<String, Object> body) {

        // 요청 본문에서 데이터 추출
        String topic = body.get("topic").toString();
        String message = body.get("message").toString();
        int count = Integer.parseInt(body.get("count").toString());

        // Solace Events Broker에 게시
        return publisherService.publish(topic, message, count);
    }
}