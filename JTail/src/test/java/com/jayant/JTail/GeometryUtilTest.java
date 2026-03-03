package com.jayant.JTail;

import com.jayant.JTail.dto.PointDto;
import com.jayant.JTail.utils.GeometryUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Point;

import static org.junit.jupiter.api.Assertions.*;

class GeometryUtilTest {

    @Test
    @DisplayName("createPoint sets correct coordinates from PointDto")
    void testCreatePoint_FromPointDto() {
        PointDto dto = new PointDto(new double[]{77.5946, 12.9716});
        Point point  = GeometryUtil.createPoint(dto);

        assertNotNull(point);
        assertEquals(77.5946, point.getX(), 0.0001);
        assertEquals(12.9716, point.getY(), 0.0001);
        assertEquals(4326, point.getSRID());
    }

    @Test
    @DisplayName("createPoint from lon/lat directly")
    void testCreatePoint_FromDirectValues() {
        Point point = GeometryUtil.createPoint(72.8777, 19.0760);
        assertEquals(72.8777, point.getX(), 0.0001);
        assertEquals(19.0760, point.getY(), 0.0001);
    }

    @Test
    @DisplayName("Haversine: Bengaluru to Mumbai ≈ 840–850 km")
    void testHaversineDistance_BengaluruToMumbai() {
        double distance = GeometryUtil.haversineDistance(
                12.9716, 77.5946,
                19.0760, 72.8777
        );

        assertTrue(distance > 800 && distance < 900,
                "Expected ~840 km, got: " + distance);
    }

    @Test
    @DisplayName("Haversine: same point should return 0")
    void testHaversineDistance_SamePoint() {
        double distance = GeometryUtil.haversineDistance(
                12.9716, 77.5946,
                12.9716, 77.5946
        );
        assertEquals(0.0, distance, 0.001);
    }
}