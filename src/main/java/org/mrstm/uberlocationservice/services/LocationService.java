package org.mrstm.uberlocationservice.services;

import org.mrstm.uberlocationservice.dto.CheckIfWithinDestDto;
import org.mrstm.uberlocationservice.dto.DriverLocationDto;
import org.mrstm.uberlocationservice.models.Location;

import java.util.List;

public interface LocationService {
    Boolean saveDriverLocation(String driverId, Double latitude, Double longitude);

    List<DriverLocationDto> getNearbyDrivers(Double latitude, Double longitude);

    Location getCurrentLocationOfDriver(String driverId);

    Boolean checkAtDestination(CheckIfWithinDestDto checkIfWithinDestDto);
}
