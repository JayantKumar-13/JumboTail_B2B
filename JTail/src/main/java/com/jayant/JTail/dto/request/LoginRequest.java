package com.jayant.JTail.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

// DTO for login requests, with validation annotations to ensure email and password are provided and properly formatted.
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class LoginRequest {

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Password is required")
    private String password;
}
