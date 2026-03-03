package com.jayant.JTail.service.impl;

import com.jayant.JTail.dto.PointDto;
import com.jayant.JTail.dto.request.CustomerSignupRequest;
import com.jayant.JTail.dto.request.LoginRequest;
import com.jayant.JTail.dto.request.SellerSignupRequest;
import com.jayant.JTail.dto.response.AuthResponse;
import com.jayant.JTail.entity.Customer;
import com.jayant.JTail.entity.Seller;
import com.jayant.JTail.entity.User;
import com.jayant.JTail.enums.UserRole;
import com.jayant.JTail.exception.DuplicateResourceException;
import com.jayant.JTail.repository.CustomerRepository;
import com.jayant.JTail.repository.SellerRepository;
import com.jayant.JTail.repository.UserRepository;
import com.jayant.JTail.security.JwtTokenProvider;
import com.jayant.JTail.service.interfaces.AuthService;
import com.jayant.JTail.utils.GeometryUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final UserRepository        userRepository;
    private final CustomerRepository    customerRepository;
    private final SellerRepository      sellerRepository;
    private final PasswordEncoder       passwordEncoder;
    private final JwtTokenProvider      jwtTokenProvider;
    private final AuthenticationManager authenticationManager;

    @Override
    @Transactional
    public AuthResponse signupCustomer(CustomerSignupRequest request) {
        log.info("Signing up new customer: {}", request.getEmail());

        // 1. Check email uniqueness
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException(
                    "Email already registered: " + request.getEmail());
        }

        // 2. Check phone uniqueness
        if (customerRepository.existsByPhone(request.getPhone())) {
            throw new DuplicateResourceException(
                    "Phone number already registered: " + request.getPhone());
        }

        // 3. Create the User auth record
        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(UserRole.CUSTOMER)
                .active(true)
                .build();
        user = userRepository.save(user);

        // 4. Create the Customer profile
        Customer customer = Customer.builder()
                .storeName(request.getStoreName())
                .ownerName(request.getOwnerName())
                .phone(request.getPhone())
                .address(request.getAddress())
                .city(request.getCity())
                .state(request.getState())
                .pincode(request.getPincode())
                .gstNumber(request.getGstNumber())
                .location(GeometryUtil.createPoint(request.getLocation()))
                .user(user)
                .build();
        customerRepository.save(customer);

        // 5. Generate and return JWT
        String token = jwtTokenProvider.generateTokenFromEmail(user.getEmail());
        log.info("Customer signup successful: {}", user.getEmail());

        return AuthResponse.builder()
                .token(token)
                .tokenType("Bearer")
                .userId(user.getId())
                .email(user.getEmail())
                .role(user.getRole())
                .message("Customer registered successfully! Welcome to Jumbotail.")
                .build();
    }

    @Override
    @Transactional
    public AuthResponse signupSeller(SellerSignupRequest request) {
        log.info("Signing up new seller: {}", request.getEmail());

        // 1. Check email uniqueness
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException(
                    "Email already registered: " + request.getEmail());
        }

        // 2. Check GST uniqueness
        if (sellerRepository.existsByGstNumber(request.getGstNumber())) {
            throw new DuplicateResourceException(
                    "GST number already registered: " + request.getGstNumber());
        }

        // 3. Create User auth record
        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(UserRole.SELLER)
                .active(true)
                .build();
        user = userRepository.save(user);

        // 4. Create Seller profile
        Seller seller = Seller.builder()
                .businessName(request.getBusinessName())
                .contactName(request.getContactName())
                .phone(request.getPhone())
                .address(request.getAddress())
                .city(request.getCity())
                .state(request.getState())
                .pincode(request.getPincode())
                .gstNumber(request.getGstNumber())
                .location(GeometryUtil.createPoint(request.getLocation()))
                .user(user)
                .build();
        sellerRepository.save(seller);

        String token = jwtTokenProvider.generateTokenFromEmail(user.getEmail());
        log.info("Seller signup successful: {}", user.getEmail());

        return AuthResponse.builder()
                .token(token)
                .tokenType("Bearer")
                .userId(user.getId())
                .email(user.getEmail())
                .role(user.getRole())
                .message("Seller registered successfully!")
                .build();
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        log.info("Login attempt for: {}", request.getEmail());

        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow();

        String token = jwtTokenProvider.generateToken(auth);
        log.info("Login successful for: {}", request.getEmail());

        return AuthResponse.builder()
                .token(token)
                .tokenType("Bearer")
                .userId(user.getId())
                .email(user.getEmail())
                .role(user.getRole())
                .message("Login successful!")
                .build();
    }
}
