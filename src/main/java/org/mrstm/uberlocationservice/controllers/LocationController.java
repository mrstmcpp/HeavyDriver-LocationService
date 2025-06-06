package org.mrstm.uberlocationservice.controllers;

import org.mrstm.uberlocationservice.dto.NearbyDriversRequestDto;
import org.mrstm.uberlocationservice.dto.SaveDriverLocationRequestDto;
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

    private final StringRedisTemplate stringRedisTemplate;

    private static final String DRIVER_LOCATION_KEY = "drivers";
    private static final Double SEARCH_RADIUS_KEY = 5.0;

    public LocationController(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    @GetMapping("/")
    public String home(){
        return "Hello World";
    }

    @PostMapping("/drivers")
    public ResponseEntity<Boolean> saveDriverLocation(@RequestBody SaveDriverLocationRequestDto driverLocation) {
        try{
            GeoOperations<String , String> geoOps = stringRedisTemplate.opsForGeo(); //geooperations is primarily used for storing geo coordinates
            geoOps.add(DRIVER_LOCATION_KEY ,
                    new RedisGeoCommands.GeoLocation<>(driverLocation.getDriverId() ,
                            new Point(driverLocation.getLatitude(),
                                    driverLocation.getLongitude())));
            return new ResponseEntity<>(true, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(false , HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/nearby/drivers")
    public ResponseEntity<List<String>> getNearbyDrivers(@RequestBody NearbyDriversRequestDto nearbyDriversRequestDto) {
        try{
            GeoOperations<String , String> geoOps = stringRedisTemplate.opsForGeo();
            Distance radius = new Distance(SEARCH_RADIUS_KEY , Metrics.KILOMETERS);
            Circle within = new Circle(new Point(nearbyDriversRequestDto.getLatitude() , nearbyDriversRequestDto.getLongitude()) , radius);

            GeoResults<RedisGeoCommands.GeoLocation<String>> geoResult = geoOps.radius(DRIVER_LOCATION_KEY , within);
            List<String> nearbyDrivers = new ArrayList<>();
            for(GeoResult<RedisGeoCommands.GeoLocation<String>> result : geoResult) {
                nearbyDrivers.add(result.getContent().getName());
            }
            return new ResponseEntity<>(nearbyDrivers, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
        }


    }

}
