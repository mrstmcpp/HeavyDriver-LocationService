package org.mrstm.uberlocationservice.services;

import org.mrstm.uberentityservice.dto.location.DriverLocation;
import org.mrstm.uberentityservice.models.ExactLocation;
import org.mrstm.uberlocationservice.Maths.GetDistanceBetweenTwoPoints;
import org.mrstm.uberlocationservice.dto.CheckIfWithinDestDto;
import org.mrstm.uberlocationservice.models.Location;
import org.mrstm.uberlocationservice.producers.DriverLocationPublisher;
import org.springframework.data.geo.*;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.GeoOperations;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class RedisLocationServiceImpl implements LocationService {

    private static final String DRIVER_LOCATION_KEY = "drivers:location";
    private static final Double SEARCH_RADIUS_KEY = 5.0;
    private static final Double NEAR_LOCATION_KEY = 0.5;
    private final GetDistanceBetweenTwoPoints getDistanceBetweenTwoPoints;
    private final StringRedisTemplate stringRedisTemplate;
    private final DriverLocationPublisher driverLocationPublisher;

    public RedisLocationServiceImpl(StringRedisTemplate stringRedisTemplate, DriverLocationPublisher driverLocationPublisher) {
        this.stringRedisTemplate = stringRedisTemplate;
        this.driverLocationPublisher = driverLocationPublisher;
        this.getDistanceBetweenTwoPoints = new GetDistanceBetweenTwoPoints();
    }

    @Override
    public Boolean saveDriverLocation(String driverId, Location location) {
        try {
            GeoOperations<String, String> geoOps = stringRedisTemplate.opsForGeo(); //geooperations is primarily used for storing geo coordinates
            geoOps.add(DRIVER_LOCATION_KEY, new RedisGeoCommands.GeoLocation<>(driverId, new Point(location.getLongitude(), location.getLatitude())));
            System.out.println("Saving Driver " + driverId + " at: " + location.getLatitude() + ", " + location.getLongitude());
            DriverLocation driverLocation = DriverLocation.builder()
                    .driverId(driverId)
                    .latitude(location.getLatitude())
                    .longitude(location.getLongitude())
                    .build();
            setDriverActive(driverId);
            driverLocationPublisher.publish(driverLocation);
            return true;
        } catch (RuntimeException e) {
            return false;
        }
    }

    @Override
    public List<DriverLocation> getNearbyDrivers(ExactLocation pickupLocation) {
        try {
            GeoOperations<String, String> geoOps = stringRedisTemplate.opsForGeo();
            SetOperations<String, String> setOps = stringRedisTemplate.opsForSet();

            // search radius and circle
            Distance radius = new Distance(SEARCH_RADIUS_KEY, Metrics.KILOMETERS);
            Circle within = new Circle(new Point(pickupLocation.getLongitude(), pickupLocation.getLatitude()), radius);
//            System.out.println("Searching for drivers near: " + pickupLocation.getLatitude() + ", " + pickupLocation.getLongitude());

            // problem fixed hereeeeeee

            RedisGeoCommands.GeoRadiusCommandArgs args = RedisGeoCommands.GeoRadiusCommandArgs.newGeoRadiusArgs()
                    .includeCoordinates() //telling Redis to return the coordinates
                    .sortAscending();    //sorting

            GeoResults<RedisGeoCommands.GeoLocation<String>> geoResults = geoOps.radius(DRIVER_LOCATION_KEY, within, args);

            if (geoResults == null || geoResults.getContent().isEmpty()) {
                System.out.println("No drivers found in radius.");
                return Collections.emptyList();
            }

            // filter only active drivers
            List<DriverLocation> activeNearbyDrivers = new ArrayList<>();
            for (GeoResult<RedisGeoCommands.GeoLocation<String>> result : geoResults) {
                String driverId = result.getContent().getName();
//                System.out.println("DRIVER ID : " + driverId);
//                System.out.println(result.getContent().toString());

                // check for active status
                Boolean isActive = setOps.isMember("drivers:active", driverId);
                if (Boolean.TRUE.equals(isActive)) {

                    Point point = result.getContent().getPoint();

                    if (point == null) { //skip inactive drivers
//                        System.err.println("Driver " + driverId + " has a null location in Redis.");
                        continue;
                    }

                    DriverLocation driverLocation = DriverLocation.builder()
                            .driverId(driverId)
                            .latitude(point.getY())
                            .longitude(point.getX())
                            .build();
                    activeNearbyDrivers.add(driverLocation);

                } else {
                    System.out.println("Driver " + driverId + " is nearby but not active.");
                }
            }

            return activeNearbyDrivers;

        } catch (RuntimeException e) {
            System.err.println("Error getting nearby drivers: " + e.getMessage());
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    @Override
    public Location getCurrentLocationOfDriver(String driverId) {
        GeoOperations<String, String> geoOps = stringRedisTemplate.opsForGeo();
        Point point = geoOps.position(DRIVER_LOCATION_KEY, driverId).get(0);
        return Location.builder().latitude(point.getY()).longitude(point.getX()).build();
    }

    @Override
    public Boolean checkAtDestination(CheckIfWithinDestDto checkIfWithinDestDto) {
        Location currentLocation = getCurrentLocationOfDriver(checkIfWithinDestDto.getDriverId());
        double distance = getDistanceBetweenTwoPoints.getDistance(currentLocation, checkIfWithinDestDto.getEndLocation());
        return distance <= 0.5;
    }

    @Override
    public void setDriverActive(String driverId) {
        try {
            stringRedisTemplate.opsForSet().add("drivers:active", driverId);
            //auto-deletion set to large value for dev
            //set to ofMinutes();
            stringRedisTemplate.opsForValue().set("driver:lastActive:" + driverId, "1", Duration.ofDays(100));
        } catch (Exception e) {
            System.err.println("Error setting driver active: " + e.getMessage());
        }
    }

    @Override
    public void setDriverInactive(String driverId) {
        try {
            stringRedisTemplate.opsForSet().remove("drivers:active", driverId);
            stringRedisTemplate.delete("driver:lastActive:" + driverId);
        } catch (Exception e) {
            System.err.println("Error setting driver inactive: " + e.getMessage());
        }
    }
}
