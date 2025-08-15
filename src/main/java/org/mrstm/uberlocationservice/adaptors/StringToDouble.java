package org.mrstm.uberlocationservice.adaptors;

import org.mrstm.uberlocationservice.dto.NearbyDriversRequestDto;
import org.mrstm.uberlocationservice.models.Location;
import org.springframework.stereotype.Component;

@Component
public class StringToDouble {
    public Location convertToDouble(NearbyDriversRequestDto nearbyDriversRequestDto) {
        if (nearbyDriversRequestDto == null || nearbyDriversRequestDto.getLatitude() == null || nearbyDriversRequestDto.getLongitude() == null) {
            throw new IllegalArgumentException("Latitude and longitude cannot be null");
        }

        try {
            double latitude = Double.parseDouble(nearbyDriversRequestDto.getLatitude());
            double longitude = Double.parseDouble(nearbyDriversRequestDto.getLongitude());

            return Location.builder()
                    .latitude(latitude)
                    .longitude(longitude)
                    .build();

        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid latitude or longitude format", e);
        }
    }
}
