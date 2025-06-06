package org.mrstm.uberlocationservice.dto;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NearbyDriversRequestDto {
    Double latitude;
    Double longitude;

}
