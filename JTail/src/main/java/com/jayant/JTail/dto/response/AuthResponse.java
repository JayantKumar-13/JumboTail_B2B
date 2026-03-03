package com.jayant.JTail.dto.response;

import com.jayant.JTail.enums.UserRole;
import lombok.*;

// DTO for authentication responses, containing the JWT token and user details to be returned upon successful login or signup.
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class AuthResponse {
    private String token;
    private String tokenType = "Bearer";
    private Long userId;
    private String email;
    private UserRole role;
    private String message;
}
