package org.mrstm.uberlocationservice.consumers;


import org.mrstm.uberentityservice.dto.booking.BookingCreatedEvent;
import org.mrstm.uberentityservice.dto.booking.NearbyDriverEvent;
import org.mrstm.uberentityservice.dto.location.DriverLocation;
import org.mrstm.uberlocationservice.kafka.KafkaTopics;
import org.mrstm.uberlocationservice.services.KafkaService;
import org.mrstm.uberlocationservice.services.LocationService;
import org.springframework.kafka.annotation.KafkaListener;

import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class BookingCreatedConsumer {
    private final LocationService locationService;
    private final KafkaService kafkaService;


    public BookingCreatedConsumer(LocationService locationService, KafkaService kafkaService) {
        this.locationService = locationService;
        this.kafkaService = kafkaService;
    }

    @KafkaListener(topics = KafkaTopics.BOOKING_CREATED, groupId = "location-group")
    public void consumeBookingCreated(BookingCreatedEvent payload) { // this function send driver list to socket for producing notifications
        System.out.println(payload.getBookingId());
        List<DriverLocation> nearbyDrivers = locationService.getNearbyDrivers(payload.getPickupLocation());
        NearbyDriverEvent nearbyDriverEvent = NearbyDriverEvent.builder()
                .bookingId(payload.getBookingId())
                .passengerId(payload.getPassengerId())
                .pickupLocation(payload.getPickupLocation())
                .dropLocation(payload.getDropLocation())
                .driverLocationList(nearbyDrivers).build();
        kafkaService.publishDriverLocations(nearbyDriverEvent); //send it for producing nearby driver list
    }


}
