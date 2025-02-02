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
    private String topic;
    private String queueName;
    private String message;
    DeliveryMode deliveryMode;
    private int count;
}
