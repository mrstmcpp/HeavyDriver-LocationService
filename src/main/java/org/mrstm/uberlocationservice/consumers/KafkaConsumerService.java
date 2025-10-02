package org.mrstm.uberlocationservice.consumers;

import org.json.JSONObject;
import org.mrstm.uberlocationservice.services.LocationService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumerService {
    private final LocationService locationService;

    public KafkaConsumerService(LocationService locationService){
        this.locationService = locationService;
    }
    @KafkaListener(topics = "driver-location", groupId = "${spring.kafka.consumer.group-id}")
    public void consumeLocation(String message) {
        JSONObject json = new JSONObject(message);
        String driverId = json.getString("driverId");
        double lat = json.getDouble("lat");
        double lon = json.getDouble("lon");

        try{
            Boolean res = locationService.saveDriverLocation(driverId , lat, lon);
            if(res){
                System.out.println("Saved");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        System.out.println(message);
//        redisTemplate.opsForGeo().add("driver-locations", new Point(lon, lat), driverId);
    }




}
