package com.jayant.JTail.dto.response;

import lombok.*;
// DTO for customer details to be returned in API responses, containing all relevant information about the customer including contact details and location.
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class CustomerResponse {
    private Long id;
    private String storeName;
    private String ownerName;
    private String phone;
    private String address;
    private String city;
    private String state;
    private String pincode;
    private String gstNumber;
    private String email;
    private WarehouseResponse.LocationDto location;
}
