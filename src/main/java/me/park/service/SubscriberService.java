package me.park.service;

import me.park.solace.Subscriber;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * SubscriberService:
 * 메시지 구독 처리를 담당하는 서비스 클래스입니다.
 */
@Service
public class SubscriberService {

    private final Subscriber subscriber;

    @Autowired
    public SubscriberService(Subscriber subscriber) {
        this.subscriber = subscriber;
    }

    //Events Broker Connect
    public String connect() {
        try {
            subscriber.connect();   //실제 Connect실행
            return "Subscriber connected successfully to Solace Event Broker.";
        } catch (Exception e) {
            return "Failed to connect Subscriber: " + e.getMessage();
        }
    }

    //Events Broker Sub 구독
    public String subscribe(String topic) {
        try {
            subscriber.subscribe(topic);    //실제 Sub구독처리
            return "Successfully subscribed to topic: " + topic;
        } catch (Exception e) {
            return "Failed to subscribe to topic: " + topic + ". Error: " + e.getMessage();
        }
    }
}