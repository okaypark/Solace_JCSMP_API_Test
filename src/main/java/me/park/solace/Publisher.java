package me.park.solace;

import com.solacesystems.jcsmp.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Publisher:
 * 메시지를 Solace로 발행하는 로직을 구현한 컴포넌트입니다.
 */
@Component
public class Publisher {

    private final JCSMPSession session;
    private XMLMessageProducer producer;

    //JSCMPSession 생성자 의존성주입
    @Autowired
    public Publisher(JCSMPSession session) {
        this.session = session;
    }

    //Solace JSCMP 세션 연결
    public void connect() {
        try {
            if (!session.isClosed()) {
                //JSCMP Session으로 Solace Broker 연결
                session.connect();

                //이벤트처리위한 객체생성:producer
                //이벤트처리 핸들러 등록(성공실패처리)
                producer = session.getMessageProducer(new JCSMPStreamingPublishEventHandler() {

                    //대기하다가 발행이 실제 처리되면 호출됨
                    @Override
                    public void responseReceived(String messageID) {
                        System.out.println("Message published successfully: " + messageID);
                    }

                    //발행 오류시 호출됨
                    @Override
                    public void handleError(String messageID, JCSMPException e, long timestamp) {
                        System.err.println("Failed to publish message: " + e.getMessage());
                    }
                });
                System.out.println("Publisher connected to Solace Event Broker.");
            }
        } catch (JCSMPException e) {
            System.err.println("Error connecting Publisher to Solace Event Broker: " + e.getMessage());
        }
    }

    //Solace JSCMP 게시처리 (topic, message)
    public void publish(String topic, String message, DeliveryMode deliveryMode) {
        try {
            //
            TextMessage textMessage = JCSMPFactory.onlyInstance().createMessage(TextMessage.class);

            //메세지 모드 (DIRECT OR PERSISTENT)
            if (deliveryMode == DeliveryMode.PERSISTENT) {
                textMessage.setDeliveryMode(DeliveryMode.PERSISTENT);
            } else if (deliveryMode.equals("NON_PERSISTENT")) {
                textMessage.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
            }
            //PRINT CHECK
            System.out.println("## Publish Delivery Mode : " + (deliveryMode.equals("PERSISTENT")? "PERSISTENT" : "DIRECT"));

            textMessage.setText(message);
            Topic solaceTopic = JCSMPFactory.onlyInstance().createTopic(topic);
            producer.send(textMessage, solaceTopic);
            System.out.println("Message sent to topic [" + topic + "]: " + message);
        } catch (JCSMPException e) {
            System.err.println("Error sending message: " + e.getMessage());
        }
    }
}