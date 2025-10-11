package org.mrstm.uberlocationservice.controllers;

import jakarta.validation.Valid;
import org.mrstm.uberentityservice.dto.location.DriverLocation;
import org.mrstm.uberentityservice.models.ExactLocation;
import org.mrstm.uberlocationservice.adaptors.StringToDouble;
import org.mrstm.uberlocationservice.dto.CheckIfWithinDestDto;
import org.mrstm.uberlocationservice.dto.NearbyDriversRequestDto;
import org.mrstm.uberlocationservice.dto.SaveDriverLocationRequestDto;
import org.mrstm.uberlocationservice.models.Location;
import org.mrstm.uberlocationservice.services.LocationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/location")
public class LocationController {

    private final LocationService locationService;
    private final StringToDouble adaptor;


    public LocationController(LocationService locationService, StringToDouble adaptor) {
        this.locationService = locationService;
        this.adaptor = adaptor;
    }

    @PostMapping("/drivers/location")
    public ResponseEntity<?> saveDriverLocation(@Valid @RequestBody SaveDriverLocationRequestDto dto) {
        try {
            boolean saved = locationService.saveDriverLocation(dto.getDriverId(), dto.getLatitude(), dto.getLongitude());
            if (saved) {
                return ResponseEntity.status(HttpStatus.CREATED)
                        .body(Map.of("success", true, "message", "Location saved successfully"));
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("success", false, "message", "Failed to save location"));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "error", e.getMessage()));
        }
    }


    @PostMapping("/nearby/drivers")
    public ResponseEntity<List<DriverLocation>> getNearbyDrivers(@RequestBody NearbyDriversRequestDto nearbyDriversRequestDto) {
        try{
            Location location = adaptor.convertToDouble(nearbyDriversRequestDto);
            ExactLocation loc = ExactLocation.builder().latitude(location.getLatitude()).longitude(location.getLongitude()).build();
            List<DriverLocation> nearbyDrivers = locationService.getNearbyDrivers(loc);
            return new ResponseEntity<>(nearbyDrivers, HttpStatus.OK);
        } catch (Exception e) {
            System.out.println(e);
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @GetMapping("/driver/{driverId}")
    public ResponseEntity<Location> getDriverLocation(@PathVariable long driverId) {

        Location location = locationService.getCurrentLocationOfDriver(String.valueOf(driverId));

        if (location == null) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build(); // or NOT_FOUND
        }
        return ResponseEntity.ok(location);
    }


    @PostMapping("/driver/verifyLocation/{driverId}")
    public ResponseEntity<Boolean> verifyDriverLocation(@PathVariable long driverId , @RequestBody Location location){
        CheckIfWithinDestDto checkIfWithinDestDto = CheckIfWithinDestDto.builder()
                .driverId(String.valueOf(driverId))
                .endLocation(location)
                .build();
        Boolean response = locationService.checkAtDestination(checkIfWithinDestDto);
        return ResponseEntity.ok(response);
    }
}
