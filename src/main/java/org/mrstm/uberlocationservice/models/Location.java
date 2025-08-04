package org.mrstm.uberlocationservice.models;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class Location {
    private Double latitude;
    private Double longitude;
}
