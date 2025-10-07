package org.mrstm.uberlocationservice.services;

import org.mrstm.uberentityservice.dto.location.DriverLocation;
import org.mrstm.uberentityservice.models.ExactLocation;
import org.mrstm.uberlocationservice.Maths.GetDistanceBetweenTwoPoints;
import org.mrstm.uberlocationservice.dto.CheckIfWithinDestDto;
import org.mrstm.uberlocationservice.models.Location;
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
    private static final Double NEAR_LOCATION_KEY = 0.5;
    private final GetDistanceBetweenTwoPoints getDistanceBetweenTwoPoints;
    private final StringRedisTemplate stringRedisTemplate;

    public RedisLocationServiceImpl(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
        this.getDistanceBetweenTwoPoints = new GetDistanceBetweenTwoPoints();
    }


    @Override
    public Boolean saveDriverLocation(String driverId, Double latitude, Double longitude) {
       try{
           GeoOperations<String, String> geoOps = stringRedisTemplate.opsForGeo(); //geooperations is primarily used for storing geo coordinates
           geoOps.add(DRIVER_LOCATION_KEY,
                   new RedisGeoCommands.GeoLocation<>(driverId,
                           new Point(latitude, longitude)));

           return true;
       } catch (RuntimeException e) {
           return false;
       }
    }

    @Override
    public List<DriverLocation> getNearbyDrivers(ExactLocation pickupLocation) {
        try{
            GeoOperations<String, String> geoOps = stringRedisTemplate.opsForGeo();
            Distance radius = new Distance(SEARCH_RADIUS_KEY, Metrics.KILOMETERS);
            Circle within = new Circle(new Point(pickupLocation.getLatitude() , pickupLocation.getLongitude()), radius);

            GeoResults<RedisGeoCommands.GeoLocation<String>> geoResult = geoOps.radius(DRIVER_LOCATION_KEY, within);
            List<DriverLocation> nearbyDrivers = new ArrayList<>();
            for (GeoResult<RedisGeoCommands.GeoLocation<String>> result : geoResult) {
                Point point = geoOps.position(DRIVER_LOCATION_KEY, result.getContent().getName()).get(0);
                DriverLocation driverLocationDto = DriverLocation.builder()
                        .driverId(result.getContent().getName())
                        .latitude(point.getX())
                        .longitude(point.getY())
                        .build();
                nearbyDrivers.add(driverLocationDto);
            }
            return nearbyDrivers;
        } catch (RuntimeException e) {
            return null;
        }
    }

    @Override
    public Location getCurrentLocationOfDriver(String driverId) {
        GeoOperations<String, String> geoOps = stringRedisTemplate.opsForGeo();
        Point point = geoOps.position(DRIVER_LOCATION_KEY, driverId).get(0);
        return Location.builder()
                .latitude(point.getX())
                .longitude(point.getY())
                .build();
    }


    @Override
    public Boolean checkAtDestination(CheckIfWithinDestDto checkIfWithinDestDto) {
        Location currentLocation = getCurrentLocationOfDriver(checkIfWithinDestDto.getDriverId());
        double distance = getDistanceBetweenTwoPoints.getDistance(currentLocation , checkIfWithinDestDto.getEndLocation());
        return distance <= 0.5;
    }


}
