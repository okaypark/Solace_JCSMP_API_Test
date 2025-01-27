package me.park.solace;

import com.solacesystems.jcsmp.*;
import me.park.util.MessageWebSocketHandler;
import org.springframework.stereotype.Component;

@Component
public class Subscriber {

    private final JCSMPSession session;
    private final MessageWebSocketHandler webSocketHandler;

    public Subscriber(JCSMPSession session, MessageWebSocketHandler webSocketHandler) {
        this.session = session;
        this.webSocketHandler = webSocketHandler;
    }

    /**
     * Solace 세션 연결
     */
    public void connect() {
        try {
            if (!session.isClosed()) {
                session.connect();
                System.out.println("Subscriber connected to Solace Event Broker.");
            }
        } catch (JCSMPException e) {
            System.err.println("Error connecting Subscriber to Solace Event Broker: " + e.getMessage());
        }
    }

    /**
     * 특정 Topic 구독
     *
     * @param topic 구독할 토픽 이름
     */
    public void subscribe(String topic) {
        try {
            XMLMessageConsumer consumer = session.getMessageConsumer(new XMLMessageListener() {
                @Override
                public void onReceive(BytesXMLMessage msg) {
                    if (msg instanceof TextMessage) {
                        String receivedMessage = ((TextMessage) msg).getText();
                        System.out.println("Received message: " + receivedMessage);

                        // WebSocket을 통해 프론트엔드로 메시지 전송
                        webSocketHandler.broadcast(receivedMessage);
                    }
                }

                @Override
                public void onException(JCSMPException e) {
                    System.err.println("Error receiving message: " + e.getMessage());
                }
            });

            Topic solaceTopic = JCSMPFactory.onlyInstance().createTopic(topic);
            session.addSubscription(solaceTopic);
            consumer.start();
            System.out.println("Subscribed to topic: " + topic);
        } catch (JCSMPException e) {
            System.err.println("Error subscribing to topic [" + topic + "]: " + e.getMessage());
        }
    }
}