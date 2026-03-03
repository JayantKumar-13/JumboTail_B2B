package com.jayant.JTail.service.interfaces;

import com.jayant.JTail.dto.response.CustomerResponse;

public interface CustomerService {

    CustomerResponse getCustomerById(Long customerId);

    CustomerResponse getCustomerByEmail(String email);
}
