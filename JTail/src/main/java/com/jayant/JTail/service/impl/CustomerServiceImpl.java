package com.jayant.JTail.service.impl;

import com.jayant.JTail.dto.response.CustomerResponse;
import com.jayant.JTail.dto.response.WarehouseResponse;
import com.jayant.JTail.entity.Customer;
import com.jayant.JTail.exception.ResourceNotFoundException;
import com.jayant.JTail.repository.CustomerRepository;
import com.jayant.JTail.service.interfaces.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;

    @Override
    @Transactional(readOnly = true)
    public CustomerResponse getCustomerById(Long customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", "id", customerId));
        return toResponse(customer);
    }

    @Override
    @Transactional(readOnly = true)
    public CustomerResponse getCustomerByEmail(String email) {
        Customer customer = customerRepository.findByUserEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Customer not found for email: " + email));
        return toResponse(customer);
    }

    private CustomerResponse toResponse(Customer customer) {
        WarehouseResponse.LocationDto locationDto = null;
        if (customer.getLocation() != null) {
            locationDto = new WarehouseResponse.LocationDto(
                    customer.getLocation().getY(),  // lat
                    customer.getLocation().getX()   // lng
            );
        }
        return CustomerResponse.builder()
                .id(customer.getId())
                .storeName(customer.getStoreName())
                .ownerName(customer.getOwnerName())
                .phone(customer.getPhone())
                .address(customer.getAddress())
                .city(customer.getCity())
                .state(customer.getState())
                .pincode(customer.getPincode())
                .gstNumber(customer.getGstNumber())
                .email(customer.getUser().getEmail())
                .location(locationDto)
                .build();
    }
}
