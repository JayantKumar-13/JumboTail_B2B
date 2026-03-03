package com.jayant.JTail.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.*;

// DTO for representing a geographical point with longitude and latitude coordinates, used for warehouse location and distance calculations.
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class PointDto {

    @NotNull(message = "Coordinates are required")
    private double[] coordinates;

    public double getLongitude() {
        return coordinates[0];
    }

    public double getLatitude() {
        return coordinates[1];
    }
}
