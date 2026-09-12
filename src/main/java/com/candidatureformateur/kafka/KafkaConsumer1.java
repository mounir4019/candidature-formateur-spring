/* package com.candidatureformateur.kafka;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumer1 {

    @KafkaListener(topics = "myTopic", groupId = "myGroup1")
    public void listen1(String message) {
        System.out.println("Message reçu1 : " + message);
    }
}
 */