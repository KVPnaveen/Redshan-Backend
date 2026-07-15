package com.redshanflora.redshanflora_backend.service;

import com.redshanflora.redshanflora_backend.dto.OrderListDto;

import java.util.List;

public interface OrderService {

    List<OrderListDto> getAllOrders();

}