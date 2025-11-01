package org.mrstm.uberlocationservice.producers;

import org.mrstm.uberentityservice.dto.location.DriverLocation;
import org.mrstm.uberentityservice.kafkaTopics.KafkaTopics;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class DriverLocationPublisher {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public DriverLocationPublisher(KafkaTemplate<String, Object> kafkaTemplate){
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publish(DriverLocation location){
        try {
//            System.out.println("Publishing driver location for passenger: " + location.getDriverId());
            kafkaTemplate.send(KafkaTopics.DRIVER_LOCATION, location);
        } catch (Exception e) {
            System.err.println("Failed to publish driver location: " + e.getMessage());
        }
    }
}
