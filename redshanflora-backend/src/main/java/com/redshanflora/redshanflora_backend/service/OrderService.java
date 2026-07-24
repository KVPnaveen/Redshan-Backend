package com.redshanflora.redshanflora_backend.service;

import com.redshanflora.redshanflora_backend.dto.OrderListDto;
import com.redshanflora.redshanflora_backend.dto.order.OrderDetailsDto;
import com.redshanflora.redshanflora_backend.dto.order.OrderSummaryDto;

import java.util.List;

public interface OrderService {

    // Incoming team member's function
    List<OrderListDto> getAllOrders();

    List<OrderListDto> getUnassignedOrders();


    // Your customer order functions
    List<OrderSummaryDto> getCustomerOrders();

    OrderDetailsDto getCustomerOrderDetails(Long orderId);

    // Your admin order functions
    List<OrderSummaryDto> getAllOrdersForAdmin();

    OrderDetailsDto getAdminOrderDetails(Long orderId);

}