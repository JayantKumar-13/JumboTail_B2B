package com.jayant.JTail.dto.request;

import com.jayant.JTail.dto.PointDto;
import jakarta.validation.constraints.*;
import lombok.*;

// DTO for customer signup requests, with validation annotations to ensure proper input format and required fields.
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class CustomerSignupRequest {

    @NotBlank(message = "Store name is required")
    @Size(min = 2, max = 150, message = "Store name must be 2–150 characters")
    private String storeName;

    @NotBlank(message = "Owner name is required")
    @Size(min = 2, max = 100)
    private String ownerName;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^[6-9]\\d{9}$", message = "Invalid Indian mobile number")
    private String phone;

    @NotBlank(message = "Address is required")
    private String address;

    private String city;

    private String state;

    @Pattern(regexp = "^[1-9][0-9]{5}$", message = "Invalid PIN code")
    private String pincode;

    @NotNull(message = "Location coordinates are required")
    private PointDto location;

    @Pattern(regexp = "^[0-9]{2}[A-Z]{5}[0-9]{4}[A-Z]{1}[1-9A-Z]{1}Z[0-9A-Z]{1}$",
             message = "Invalid GST number format")
    private String gstNumber;
}
