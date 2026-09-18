package com.redshanflora.redshanflora_backend.dto.payment;

import com.redshanflora.redshanflora_backend.dto.cart.CartItemDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CheckoutRequest {
    private List<CartItemDto> items;
    private String currency;
    private Double discountAmount;
    private CustomerDetailsPayload customerDetails;
    private String address;
    private String phone;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CustomerDetailsPayload {
        private String firstName;
        private String lastName;
        private String address;
        private String city;
        private String postalCode;
        private String phone;
        private String email;
        private String deliveryDate;
    }
}
