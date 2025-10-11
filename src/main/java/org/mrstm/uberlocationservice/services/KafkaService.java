package org.mrstm.uberlocationservice.services;

import org.mrstm.uberentityservice.dto.booking.NearbyDriverEvent;
import org.springframework.stereotype.Service;

@Service
public interface KafkaService {
    void publishNearbyDriverLocations(NearbyDriverEvent nearbyDriverEvent);
}
