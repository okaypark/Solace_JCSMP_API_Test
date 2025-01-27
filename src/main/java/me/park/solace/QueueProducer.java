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

    // 큐 Broker에 등록
    public Queue provisionQueue(String queueName) throws JCSMPException {
        // 큐 생성
        Queue queue = JCSMPFactory.onlyInstance().createQueue(queueName);

        // 큐 기본 설정
        EndpointProperties endpointProperties = new EndpointProperties();
        endpointProperties.setPermission(EndpointProperties.PERMISSION_DELETE);
        endpointProperties.setAccessType(EndpointProperties.ACCESSTYPE_EXCLUSIVE);
        endpointProperties.setQuota(100); // 큐의 메시지 저장 용량(100MB 설정)

        // Broker에 등록
        session.provision(queue, endpointProperties, JCSMPSession.FLAG_IGNORE_ALREADY_EXISTS);
        System.out.printf("Queue '%s' provisioned successfully.%n", queueName);
        return queue;
    }

    @SneakyThrows
    public void createTopicToQueueMapping(String queueName, String topicName, String sendMessage, int count) {

        // 큐 Broker에 등록
        Queue queue = provisionQueue(queueName);

        // Topic 연결객체 생성
        Topic topic = JCSMPFactory.onlyInstance().createTopic(topicName);

        // topicName의 토픽을 구독해서 위에서 등록된 큐로 구독 (메세지 보장)
        session.addSubscription(queue, topic, JCSMPSession.WAIT_FOR_CONFIRM);

        // 앞서 등록후 Broker로 부터 Ack 결과값 응답받기
        final XMLMessageProducer prod = session.getMessageProducer(
                new JCSMPStreamingPublishCorrelatingEventHandler() {
                    // Broker로부터 정상처리 수신시
                    @Override
                    public void responseReceivedEx(Object key) {
                        System.out.println("Producer received response for msg: " + key.toString());
                    }

                    // Broker로부터 처리실패 수신시
                    @Override
                    public void handleErrorEx(Object key, JCSMPException cause, long timestamp) {
                        System.out.printf("Producer received error for msg: %s@%s - %s%n", key.toString(), timestamp, cause);
                    }
                });

        // 토픽 메세지 Broker 큐에 전송
        TextMessage msg =  JCSMPFactory.onlyInstance().createMessage(TextMessage.class);
        msg.setDeliveryMode(DeliveryMode.PERSISTENT);
        for (int i = 1; i <= count; i++) {
            msg.setText("Message number " + i);
            msg.setCorrelationKey(i);
            prod.send(msg, topic);
        }
        System.out.println("Sent messages.");
    }
}
