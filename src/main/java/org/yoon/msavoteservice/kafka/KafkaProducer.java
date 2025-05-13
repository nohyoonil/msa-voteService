package org.yoon.msavoteservice.kafka;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class KafkaProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;

    public void send(String topic, String message) {
        try {
            kafkaTemplate.send(topic, message);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
