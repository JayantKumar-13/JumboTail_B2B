package com.jayant.JTail.utils;

import com.jayant.JTail.dto.PointDto;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;

public final class GeometryUtil {

    private static final GeometryFactory GEOMETRY_FACTORY =
            new GeometryFactory(new PrecisionModel(), 4326);

    private GeometryUtil() {}

    public static Point createPoint(PointDto pointDto) {
        Coordinate coordinate = new Coordinate(
                pointDto.getCoordinates()[0],
                pointDto.getCoordinates()[1]
        );
        return GEOMETRY_FACTORY.createPoint(coordinate);
    }

    public static Point createPoint(double longitude, double latitude) {
        Coordinate coordinate = new Coordinate(longitude, latitude);
        return GEOMETRY_FACTORY.createPoint(coordinate);
    }

    public static double haversineDistance(double lat1, double lon1,double lat2, double lon2) {
        final double R = 6371.0;

        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }
}