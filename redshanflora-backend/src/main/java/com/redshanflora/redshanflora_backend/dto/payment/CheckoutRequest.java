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
}
