package me.park.dto;

import com.solacesystems.jcsmp.DeliveryMode;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class PublishRequest {
    private String topic;
    private String message;
    DeliveryMode deliveryMode;
    private int count;
}