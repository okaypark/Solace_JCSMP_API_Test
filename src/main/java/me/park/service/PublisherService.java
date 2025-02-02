package me.park.service;

import com.solacesystems.jcsmp.DeliveryMode;
import me.park.solace.Publisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * PublisherService:
 * Event Broker에 처리를 담당하는 서비스 클래스입니다.
 */
@Service
public class PublisherService {

    // 실제 Events Broker 통신처리 담당클래스
    private final Publisher publisher;

    // Publisher를  Bean에 의존성 주입(이객체에서 Publisher 사용한다.)
    @Autowired
    public PublisherService(Publisher publisher) {
        this.publisher = publisher;
    }

    //Event Broker Connect
    public String connect() {
        try {
            publisher.connect();
            return "Publisher connected successfully to Solace Event Broker.";
        } catch (Exception e) {
            return "Failed to connect Publisher: " + e.getMessage();
        }
    }

    ////Event Broker Pub처리 (HTTP에서 받은 보낼 메세지 횟수 만큼)
    public String publish(String topic, String message, int count, DeliveryMode deliveryMode) {
        StringBuilder result = new StringBuilder();

        for (int i = 1; i <= count; i++) {
            publisher.publish(topic, message, deliveryMode); // 단일 메시지 발행
            result.append("Message ").append(i).append(": ").append(message).append("\n");
        }

        return result.toString();
    }
}