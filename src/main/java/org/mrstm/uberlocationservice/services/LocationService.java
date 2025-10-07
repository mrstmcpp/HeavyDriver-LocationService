package org.mrstm.uberlocationservice.services;

import org.mrstm.uberentityservice.dto.location.DriverLocation;
import org.mrstm.uberentityservice.models.ExactLocation;
import org.mrstm.uberlocationservice.dto.CheckIfWithinDestDto;
import org.mrstm.uberlocationservice.dto.DriverLocationDto;
import org.mrstm.uberlocationservice.models.Location;

import java.util.List;

public interface LocationService {
    Boolean saveDriverLocation(String driverId, Double latitude, Double longitude);

    List<DriverLocation> getNearbyDrivers(ExactLocation pickupLocation);

    Location getCurrentLocationOfDriver(String driverId);

    Boolean checkAtDestination(CheckIfWithinDestDto checkIfWithinDestDto);
}
