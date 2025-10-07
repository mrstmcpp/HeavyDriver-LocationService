package org.mrstm.uberlocationservice.services;

import org.mrstm.uberentityservice.dto.booking.NearbyDriverEvent;
import org.mrstm.uberentityservice.kafkaTopics.KafkaTopics;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;


@Service
public class KafkaServiceImpl implements KafkaService{
    private final KafkaTemplate<String , Object> kafkaTemplate;

    public KafkaServiceImpl(KafkaTemplate<String, Object> kafkaTemplate, LocationService locationService) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void publishDriverLocations(NearbyDriverEvent nearbyDriverEvent) {
        try {
            kafkaTemplate.send(KafkaTopics.NEARBY_DRIVERS , nearbyDriverEvent);
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
