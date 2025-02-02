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
public class ProduceQueueRequest {
    private String queueName;
    private String message;
    DeliveryMode deliveryMode;
    private int count;
}
