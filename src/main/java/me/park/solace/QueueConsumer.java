package me.park.solace;

import com.solacesystems.jcsmp.*;
import me.park.util.MessageWebSocketHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class QueueConsumer {

    private final JCSMPSession session;
    private FlowReceiver receiver = null;  // FlowReceiver를 멤버로 선언
    private volatile boolean running = true; // Consumer 상태 플래그
    private final MessageWebSocketHandler webSocketHandler;

    @Autowired
    public QueueConsumer(JCSMPSession session, MessageWebSocketHandler webSocketHandler) {
        this.session = session;
        this.webSocketHandler = webSocketHandler;
    }

    // Solace 세션 연결
    public void connect() {
        try {
            if (!session.isClosed()) {
                session.connect();
                System.out.println("Connected to Solace Broker.");
            }
        } catch (JCSMPException e) {
            System.err.println("Error connecting to Solace Broker: " + e.getMessage());
        }
    }

    // Queue 메시지 소비 메서드
    public void consumeQueue(String queueName) throws JCSMPException {
        System.out.println("consumeQueue : queue Name=" + queueName);
        // Queue 객체 생성
        Queue queue = JCSMPFactory.onlyInstance().createQueue(queueName);

        // ConsumerFlowProperties 생성 및 설정
        ConsumerFlowProperties flowProp = new ConsumerFlowProperties();
        flowProp.setEndpoint(queue);
        flowProp.setAckMode(JCSMPProperties.SUPPORTED_MESSAGE_ACK_CLIENT);

        // 메시지 리스너 설정
        receiver = session.createFlow(new  XMLMessageListener() {
            @Override
            public void onReceive(BytesXMLMessage msg) {
                try {
                    if (!running) return; // running 상태가 false면 중단
                    if (msg instanceof TextMessage) {
                        String receivedMessage = ((TextMessage) msg).getText();
                        System.out.printf("Received TextMessage: %s%n", receivedMessage);

                        // WebSocket을 통해 프론트엔드로 메시지 전송
                        webSocketHandler.broadcast(receivedMessage);

                    } else {
                        System.out.println("Received Non-Text Message.");
                    }
                    System.out.printf("Message Dump: %s%n", msg.dump());
                    msg.ackMessage(); // 메시지 ACK
                } catch (Exception e) {
                    System.err.println("Exception while consuming message: " + e.getMessage());
                }
            }

            @Override
            public void onException(JCSMPException e) {
                System.err.println("Consumer Exception: " + e.getMessage());
            }
        }, flowProp);

        // 플로우 시작
        receiver.start();
        System.out.printf("Queue Consumer started on '%s'. Waiting for messages...%n", queueName);
    }

    // disconnect 메서드 추가 (외부에서 호출 가능)
    public synchronized void disconnect() {
        try {
            running = false;  // 메시지 수신 중단 플래그 설정
            if (receiver != null) {
                receiver.close();  // FlowReceiver 종료
                System.out.println("Consumer flow closed.");
            }
            if (!session.isClosed()) {
                session.closeSession();  // Solace 세션 종료
                System.out.println("Session disconnected.");
            }
        } catch (Exception e) {
            System.err.println("Error while QueueConsumer disconnecting: " + e.getMessage());
        }
    }

    // 소멸자 (Spring Context 종료 시 실행)
    @Override
    protected void finalize() {
        disconnect(); // 서버가 종료될 때도 Consumer를 안전하게 정리
    }
}