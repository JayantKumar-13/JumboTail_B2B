package com.jayant.JTail.service.interfaces;

import com.jayant.JTail.dto.request.CustomerSignupRequest;
import com.jayant.JTail.dto.request.LoginRequest;
import com.jayant.JTail.dto.request.SellerSignupRequest;
import com.jayant.JTail.dto.response.AuthResponse;

public interface AuthService {

    AuthResponse signupCustomer(CustomerSignupRequest request);

    AuthResponse signupSeller(SellerSignupRequest request);

    AuthResponse login(LoginRequest request);
}