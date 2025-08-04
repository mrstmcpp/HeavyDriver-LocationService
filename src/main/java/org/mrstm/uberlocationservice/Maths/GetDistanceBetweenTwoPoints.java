package org.mrstm.uberlocationservice.Maths;

import org.mrstm.uberlocationservice.models.Location;

public class GetDistanceBetweenTwoPoints {

    public double getDistance(Location p1, Location p2) {
        final int EARTH_RADIUS_KM = 6371; // Approx Earth radius in KM

        double latDistance = Math.toRadians(p2.getLatitude() - p1.getLatitude());
        double lonDistance = Math.toRadians(p2.getLongitude() - p1.getLongitude());

        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(p1.getLatitude())) * Math.cos(Math.toRadians(p2.getLatitude()))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return EARTH_RADIUS_KM * c;
    }
}
