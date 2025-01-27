package me.park.dto;

import lombok.AllArgsConstructor;
import lombok.Data; // Lombok의 Data 사용 필요
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PublishRequest {
    private String topic;
    private String message;
    private int count;
}