package org.mrstm.uberlocationservice.services;

import org.mrstm.uberentityservice.dto.booking.NearbyDriverEvent;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface KafkaService {
    void publishDriverLocations(NearbyDriverEvent nearbyDriverEvent);
}
