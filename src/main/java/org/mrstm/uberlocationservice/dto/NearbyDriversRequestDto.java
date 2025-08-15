package org.mrstm.uberlocationservice.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NearbyDriversRequestDto {
    @NotBlank(message = "Latitude is required")
     private String latitude;

    @NotBlank(message = "Longitude is required")
    private String longitude;

}
