package org.mrstm.uberlocationservice.controllers;

import org.mrstm.uberlocationservice.dto.DriverLocationDto;
import org.mrstm.uberlocationservice.dto.NearbyDriversRequestDto;
import org.mrstm.uberlocationservice.dto.SaveDriverLocationRequestDto;
import org.mrstm.uberlocationservice.services.LocationService;
import org.springframework.data.geo.*;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.GeoOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/location")
public class LocationController {

    private final LocationService locationService;



    public LocationController(LocationService locationService) {
        this.locationService = locationService;
    }


    @PostMapping("/drivers")
    public ResponseEntity<Boolean> saveDriverLocation(@RequestBody SaveDriverLocationRequestDto driverLocation) {
        try{
            Boolean response = locationService.saveDriverLocation(driverLocation.getDriverId() , driverLocation.getLatitude(), driverLocation.getLongitude());
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(false , HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/nearby/drivers")
    public ResponseEntity<List<DriverLocationDto>> getNearbyDrivers(@RequestBody NearbyDriversRequestDto nearbyDriversRequestDto) {
        try{
            List<DriverLocationDto> nearbyDrivers = locationService.getNearbyDrivers(nearbyDriversRequestDto.getLatitude() , nearbyDriversRequestDto.getLongitude());
            return new ResponseEntity<>(nearbyDrivers, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
        }


    }

}
