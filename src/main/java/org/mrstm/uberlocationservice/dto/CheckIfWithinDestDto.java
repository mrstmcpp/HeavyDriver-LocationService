package org.mrstm.uberlocationservice.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.mrstm.uberlocationservice.models.Location;

@Getter
@Setter
@Builder
public class CheckIfWithinDestDto {
    private String driverId;
    private Location endLocation;
}
