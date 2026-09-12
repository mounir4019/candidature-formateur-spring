/* package com.candidatureformateur.kafka;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumer2 {

    @KafkaListener(topics = "myTopic", groupId = "myGroup2")
    public void listen2(String message) {
        System.out.println("Message reçu2 : " + message);
    }
}
 */