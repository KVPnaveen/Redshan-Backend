package com.redshanflora.redshanflora_backend.service.impl;

import com.redshanflora.redshanflora_backend.dto.OrderListDto;
import com.redshanflora.redshanflora_backend.entity.Order;
import com.redshanflora.redshanflora_backend.entity.OrderItem;
import com.redshanflora.redshanflora_backend.repository.OrderItemRepositoryNew;
import com.redshanflora.redshanflora_backend.repository.OrderRepository;
import com.redshanflora.redshanflora_backend.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepositoryNew orderItemRepository;

    @Override
    public List<OrderListDto> getAllOrders() {

        List<Order> orders = orderRepository.findAll();

        List<OrderListDto> dtoList = new ArrayList<>();

        for (Order order : orders) {

            // Fetch all items of this order
            List<OrderItem> items = orderItemRepository.findByOrderId(order.getId());

            String itemName = "";

            if (!items.isEmpty()) {
                itemName = items.get(0).getProduct().getProductName();
            }

            String type = order.getCustomizedBouquet() != null
                    ? "customize order"
                    : "not customize order";

            OrderListDto dto = OrderListDto.builder()
                    .orderId(order.getId())
                    .customerName(order.getCustomer().getUser().getName()) // Change if your User entity uses fullName/username
                    .itemName(itemName)
                    .price(order.getTotalAmount())
                    .type(type)
                    .build();

            dtoList.add(dto);
        }

        return dtoList;
    }

    @Override
    public List<OrderListDto> getUnassignedOrders() {

        List<Order> orders = orderRepository.findByEmployeeIsNull();

        List<OrderListDto> dtoList = new ArrayList<>();

        for (Order order : orders) {

            List<OrderItem> items = orderItemRepository.findByOrderId(order.getId());

            String itemName = "";

            if (!items.isEmpty()) {
                itemName = items.get(0).getProduct().getProductName();
            }

            String type = order.getCustomizedBouquet() != null
                    ? "customize order"
                    : "not customize order";

            OrderListDto dto = OrderListDto.builder()
                    .orderId(order.getId())
                    .customerName(order.getCustomer().getUser().getName())
                    .itemName(itemName)
                    .price(order.getTotalAmount())
                    .type(type)
                    .build();

            dtoList.add(dto);
        }

        return dtoList;
    }
}