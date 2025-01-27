package me.park.controller;

import me.park.service.SubscriberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * SubscriberController:
 * 메시지 구독과 관련된 SUB REST API를 제공하는 컨트롤러입니다.
 */
@RestController
@RequestMapping("/api/subscriber")
public class SubscriberController {

    private final SubscriberService subscriberService;

    //API를 실체 처리할 Service 의존성주입
    @Autowired
    public SubscriberController(SubscriberService subscriberService) {
        this.subscriberService = subscriberService;
    }

    //Events Broker Connect처리
    @PostMapping("/connect")
    public String connect() {
        return subscriberService.connect();
    }

    //Events Broker Sub 구독처리
    @PostMapping("/subscribe")
    public String subscribe(@RequestBody Map<String, Object> body) {
        String topic = body.get("topic").toString();    //HTTP에서 받은 topic 설정
        return subscriberService.subscribe(topic);
    }
}