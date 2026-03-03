package com.jayant.JTail.dto.request;

import com.jayant.JTail.dto.PointDto;
import jakarta.validation.constraints.*;
import lombok.*;

// DTO for seller signup requests, with validation annotations to ensure proper input format and required fields.
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class SellerSignupRequest {

    @NotBlank(message = "Business name is required")
    @Size(min = 2, max = 150)
    private String businessName;

    @NotBlank(message = "Contact name is required")
    @Size(min = 2, max = 100)
    private String contactName;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;

    @NotBlank(message = "Phone is required")
    @Pattern(regexp = "^[6-9]\\d{9}$", message = "Invalid Indian mobile number")
    private String phone;

    @NotBlank(message = "Address is required")
    private String address;

    private String city;

    private String state;

    @Pattern(regexp = "^[1-9][0-9]{5}$", message = "Invalid PIN code")
    private String pincode;

    @NotBlank(message = "GST number is required for sellers")
    @Pattern(regexp = "^[0-9]{2}[A-Z]{5}[0-9]{4}[A-Z]{1}[1-9A-Z]{1}Z[0-9A-Z]{1}$",
             message = "Invalid GST number format")
    private String gstNumber;

    @NotNull(message = "Location coordinates are required")
    private PointDto location;
}
