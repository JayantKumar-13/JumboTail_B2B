package com.jayant.JTail.dto.request;

import com.jayant.JTail.dto.PointDto;
import jakarta.validation.constraints.*;
import lombok.*;

// DTO for warehouse creation and update requests, with validation annotations to ensure required fields are provided and properly formatted.
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class WarehouseRequest {

    @NotBlank(message = "Warehouse name is required")
    @Size(min = 2, max = 100, message = "Warehouse name must be 2–100 characters")
    private String name;

    @NotBlank(message = "Address is required")
    private String address;

    private String city;

    private String state;

    @Pattern(regexp = "^[1-9][0-9]{5}$", message = "Invalid PIN code")
    private String pincode;

    private String contactPhone;

    @NotNull(message = "Location coordinates are required")
    private PointDto location;

    private Double capacitySqFt;
}
