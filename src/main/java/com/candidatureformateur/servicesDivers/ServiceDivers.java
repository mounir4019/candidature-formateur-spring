package com.candidatureformateur.servicesDivers;

import java.security.SecureRandom;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import org.springframework.stereotype.Service;
@Service
public class ServiceDivers {
       public static String generateRandomCode(int length) {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder randomCode = new StringBuilder();

        SecureRandom random = new SecureRandom();

        for (int i = 0; i < length; i++) {
            int randomIndex = random.nextInt(characters.length());
            randomCode.append(characters.charAt(randomIndex));
        }

        return randomCode.toString();
    }
            public static String generateRef(String prefix) {
    //String dateStr = Instant.now().format(DateTimeFormatter.ofPattern("ddMMyyyyHHmmss"));
    String dateStr = DateTimeFormatter .ofPattern("ddMMyyyyHHmmss") .withZone(ZoneOffset.UTC) .format(Instant.now());
    String uuidPart = UUID.randomUUID().toString().replace("-", "").substring(0, 8);
    return (prefix + dateStr + uuidPart).toUpperCase();
    } 
}
