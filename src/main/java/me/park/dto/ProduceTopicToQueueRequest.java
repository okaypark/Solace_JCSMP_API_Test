package me.park.dto;

import com.solacesystems.jcsmp.DeliveryMode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class ProduceTopicToQueueRequest {
    private String topicName;
    private String queueName;
    private String messageContent;
    private int count;
    DeliveryMode deliveryMode;
}
