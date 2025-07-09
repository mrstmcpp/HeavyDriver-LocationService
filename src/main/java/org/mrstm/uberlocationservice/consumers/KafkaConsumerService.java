package org.mrstm.uberlocationservice.consumers;

import org.json.JSONObject;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumerService {

    @KafkaListener(topics = "driver-location", groupId = "group-1")
    public void consumeLocation(String message) {
        System.out.println(message);
//        redisTemplate.opsForGeo().add("driver-locations", new Point(lon, lat), driverId);
    }

}
