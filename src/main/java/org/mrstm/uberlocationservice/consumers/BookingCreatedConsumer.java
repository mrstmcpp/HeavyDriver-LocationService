package org.mrstm.uberlocationservice.consumers;


import org.mrstm.uberentityservice.dto.booking.BookingCreatedEvent;
import org.mrstm.uberlocationservice.kafka.KafkaTopics;
import org.mrstm.uberlocationservice.services.LocationService;
import org.springframework.kafka.annotation.KafkaListener;

import org.springframework.stereotype.Service;



@Service
public class BookingCreatedConsumer {
    private final LocationService locationService;


    public BookingCreatedConsumer(LocationService locationService) {
        this.locationService = locationService;
    }

    @KafkaListener(topics = KafkaTopics.BOOKING_CREATED, groupId = "location-group")
    public void consumeBookingCreated(BookingCreatedEvent payload) {
        System.out.println(payload.toString());
//        List<DriverLocationDto> nearbyDrivers = locationService.getNearbyDrivers(payload.getPassengerId().toString());
    }


}
