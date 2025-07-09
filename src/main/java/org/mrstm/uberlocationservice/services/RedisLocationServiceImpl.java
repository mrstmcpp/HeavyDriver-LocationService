package org.mrstm.uberlocationservice.services;

import org.mrstm.uberlocationservice.dto.DriverLocationDto;
import org.springframework.data.geo.*;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.GeoOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class RedisLocationServiceImpl implements LocationService {

    private static final String DRIVER_LOCATION_KEY = "drivers";
    private static final Double SEARCH_RADIUS_KEY = 5.0;

    private final StringRedisTemplate stringRedisTemplate;

    public RedisLocationServiceImpl(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }


    @Override
    public Boolean saveDriverLocation(String driverId, Double latitude, Double longitude) {
        GeoOperations<String , String> geoOps = stringRedisTemplate.opsForGeo(); //geooperations is primarily used for storing geo coordinates
        geoOps.add(DRIVER_LOCATION_KEY ,
                new RedisGeoCommands.GeoLocation<>(driverId ,
                       new Point(latitude , longitude)));

        return true;
    }

    @Override
    public List<DriverLocationDto> getNearbyDrivers(Double latitude, Double longitude) {
        GeoOperations<String , String> geoOps = stringRedisTemplate.opsForGeo();
        Distance radius = new Distance(SEARCH_RADIUS_KEY, Metrics.KILOMETERS);
        Circle within = new Circle(new Point(latitude , longitude) , radius);

        GeoResults<RedisGeoCommands.GeoLocation<String>> geoResult = geoOps.radius(DRIVER_LOCATION_KEY , within);
        List<DriverLocationDto> nearbyDrivers = new ArrayList<>();
        for(GeoResult<RedisGeoCommands.GeoLocation<String>> result : geoResult) {
            Point point = geoOps.position(DRIVER_LOCATION_KEY , result.getContent().getName()).get(0);
            DriverLocationDto driverLocationDto = DriverLocationDto.builder()
                    .driverId(result.getContent().getName())
                    .latitude(point.getX())
                    .longitude(point.getY())
                    .build();
            nearbyDrivers.add(driverLocationDto);
        }
        return nearbyDrivers;
    }
}
