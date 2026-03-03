package com.jayant.JTail.service.impl;

import com.jayant.JTail.service.interfaces.OSRMService;
import com.jayant.JTail.utils.GeometryUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class OSRMServiceImpl implements OSRMService {

    private final RestTemplate restTemplate;

    @Value("${osrm.base-url}")
    private String osrmBaseUrl;
    private static final double ROAD_FACTOR = 1.3;

    @Override
    public double getDistanceKm(double fromLat, double fromLng,double toLat,   double toLng) {
        try {
            return getOsrmDistance(fromLat, fromLng, toLat, toLng);
        } catch (Exception ex) {
            log.warn("OSRM API unavailable ({}), falling back to Haversine", ex.getMessage());
            double straightLine = GeometryUtil.haversineDistance(fromLat, fromLng, toLat, toLng);
            double estimated    = straightLine * ROAD_FACTOR;
            log.info("Haversine distance: {} km, estimated road distance: {} km",
                    String.format("%.2f", straightLine),
                    String.format("%.2f", estimated));
            return estimated;
        }
    }

    @SuppressWarnings("unchecked")
    private double getOsrmDistance(double fromLat, double fromLng,
                                   double toLat,   double toLng) {

        String url = String.format(
                "%s/route/v1/driving/%s,%s;%s,%s?overview=false",
                osrmBaseUrl,
                fromLng, fromLat,   // origin: lon,lat
                toLng,   toLat      // destination: lon,lat
        );

        log.debug("Calling OSRM API: {}", url);

        // RestTemplate deserializes the JSON response into a Map
        Map<String, Object> response = restTemplate.getForObject(url, Map.class);

        if (response == null || !response.containsKey("routes")) {
            throw new RuntimeException("Invalid OSRM response: routes not found");
        }

        List<Map<String, Object>> routes =
                (List<Map<String, Object>>) response.get("routes");

        if (routes == null || routes.isEmpty()) {
            throw new RuntimeException("OSRM returned no routes between the given coordinates");
        }

        double distanceMetres = ((Number) routes.get(0).get("distance")).doubleValue();
        double distanceKm     = distanceMetres / 1000.0;

        log.info("OSRM road distance: {} km", String.format("%.2f", distanceKm));
        return distanceKm;
    }
}
