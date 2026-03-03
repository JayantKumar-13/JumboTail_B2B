package com.jayant.JTail.dto.response;

import lombok.*;

// DTO for warehouse details to be returned in API responses, containing all relevant information about the warehouse including location coordinates for distance calculations.
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class WarehouseResponse {
    private Long warehouseId;
    private String name;
    private String address;
    private String city;
    private LocationDto warehouseLocation;

    @Getter @Setter
    @NoArgsConstructor @AllArgsConstructor
    @Builder
    public static class LocationDto {
        private double lat;
        private double lng;
    }
}
