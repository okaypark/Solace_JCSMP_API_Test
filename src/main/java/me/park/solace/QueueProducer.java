package me.park.solace;

import com.solacesystems.jcsmp.*;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class QueueProducer {

    private final JCSMPSession session;
    private XMLMessageProducer producer;

    @Autowired
    public QueueProducer(JCSMPSession session) { this.session = session; }

    // 세션 권한 체크
    public void confirmSessionCapability() {
        // Confirm the current session supports the capabilities required.
        if (session.isCapable(CapabilityType.PUB_GUARANTEED) &&
                session.isCapable(CapabilityType.SUB_FLOW_GUARANTEED) &&
                session.isCapable(CapabilityType.ENDPOINT_MANAGEMENT) &&
                session.isCapable(CapabilityType.QUEUE_SUBSCRIPTIONS)) {
            System.out.println("All required capabilities supported!");
        } else {
            System.out.println("Missing required capability.");
            System.out.println("Capability - PUB_GUARANTEED: " + session.isCapable(CapabilityType.PUB_GUARANTEED));
            System.out.println("Capability - SUB_FLOW_GUARANTEED: " + session.isCapable(CapabilityType.SUB_FLOW_GUARANTEED));
            System.out.println("Capability - ENDPOINT_MANAGEMENT: " + session.isCapable(CapabilityType.ENDPOINT_MANAGEMENT));
            System.out.println("Capability - QUEUE_SUBSCRIPTIONS: " + session.isCapable(CapabilityType.QUEUE_SUBSCRIPTIONS));

            throw new IllegalStateException("Session lacks one or more required capabilities.");
        }
    }

    // Broker에 연결
    public void connect() {
        try {
            if (session.isClosed()) {
                // broker 연결
                session.connect();
                // 세션 권한 체크
                confirmSessionCapability();
            }
        } catch (JCSMPException e) {
            System.err.println("Error connecting QueueProducer to Solace Event Broker: " + e.getMessage());
        }
    }

    // 세션 종료
    public void disconnect() {
        session.closeSession();
    }

    // 공통 큐 프로비저닝 로직
    @SneakyThrows
    private Queue provision(String queueName, int quota, int permission) {
        // 큐 객체 생성
        final Queue queue = JCSMPFactory.onlyInstance().createQueue(queueName);

        // 큐 EndpointProperties 설정
        EndpointProperties endpointProps = new EndpointProperties();
        endpointProps.setAccessType(EndpointProperties.ACCESSTYPE_EXCLUSIVE); // 접근 타입, 독점, non (Exclusive 또는 Non-exclusive)
        endpointProps.setQuota(quota);                                        // 메시지 저장 용량 (MB 단위)
        endpointProps.setPermission(permission);                              // 권한 (Consume, Delete 등)

        // 큐를 브로커에 등록
        session.provision(queue, endpointProps, JCSMPSession.FLAG_IGNORE_ALREADY_EXISTS);
        System.out.printf("Queue '%s' provisioned successfully with Quota: %dMB%n",
                queueName, quota);

        return queue; // 프로비저닝된 큐 반환
    }

    // 큐에 메세지 생산
    @SneakyThrows
    public void produceQueue(String queueName, DeliveryMode deliveryMode, String message, int count) {

        // 큐 생성 및 등록
        Queue queue = provision(queueName, 100, EndpointProperties.PERMISSION_CONSUME); // 기본적으로 소비 권한 설정

        // 메시지 프로듀서 생성 (ACK 핸들러 포함)
        final XMLMessageProducer producer = session.getMessageProducer(
                new JCSMPStreamingPublishCorrelatingEventHandler() {
                    // 브로커 처리 성공 시
                    @Override
                    public void responseReceivedEx(Object key) {
                        System.out.println("Producer received to Broker response for msg: " + key.toString());
                    }

                    // 브로커 처리 실패 시
                    @Override
                    public void handleErrorEx(Object key, JCSMPException cause, long timestamp) {
                        System.out.printf("Producer received error for msg: %s@%s - %s%n", key.toString(), timestamp, cause);
                    }
                });

        // 메시지 전송
        TextMessage msg = JCSMPFactory.onlyInstance().createMessage(TextMessage.class);
        msg.setDeliveryMode(deliveryMode);  // NON_PERSISTENT OR PERSISTENT
        for (int i = 1; i <= count; i++) {
            msg.setText(message + " #" + i); // 메시지 내용 생성
            msg.setCorrelationKey(i);       // 확인 용도 Key 설정
            producer.send(msg, queue);      // 토픽에 메시지 전송
            System.out.println("Producer send Broker msg :"+msg);
        }
    }


    // 토픽을 큐에 맵핑하고 메시지 생산
    @SneakyThrows
    public void produceTopicToQueueMapping(String topicName, String queueName, DeliveryMode deliveryMode, String message, int count) {
        // 큐를 생성 (Exclusive 접근, 기본 용량 100MB, PERMISSION_CONSUME)
        Queue queue = provision(queueName, 100, EndpointProperties.PERMISSION_CONSUME);

        // Topic 객체 생성
        Topic topic = JCSMPFactory.onlyInstance().createTopic(topicName);

        // 해당 토픽을 큐에 맵핑 및 등록
        session.addSubscription(queue, topic, JCSMPSession.WAIT_FOR_CONFIRM);

        // 메시지 프로듀서 생성 (ACK 핸들러 포함)
        final XMLMessageProducer producer = session.getMessageProducer(
                new JCSMPStreamingPublishCorrelatingEventHandler() {
                    // 브로커 처리 성공 시
                    @Override
                    public void responseReceivedEx(Object key) {
                        System.out.println("Producer received to Broker response for msg:" + key.toString());
                    }

                    // 브로커 처리 실패 시
                    @Override
                    public void handleErrorEx(Object key, JCSMPException cause, long timestamp) {
                        System.out.printf("Producer received error for msg: %s@%s - %s%n", key.toString(), timestamp, cause);
                    }
                });

        // 메시지 전송
        TextMessage msg = JCSMPFactory.onlyInstance().createMessage(TextMessage.class);
        msg.setDeliveryMode(deliveryMode);  // NON_PERSISTENT OR PERSISTENT
        for (int i = 1; i <= count; i++) {
            msg.setText(message + " #" + i); // 메시지 내용 생성
            msg.setCorrelationKey(i);       // 확인 용도 Key 설정
            producer.send(msg, topic);      // 토픽에 메시지 전송
        }
    }
}
