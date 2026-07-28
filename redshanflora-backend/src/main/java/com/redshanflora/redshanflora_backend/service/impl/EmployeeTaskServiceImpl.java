package com.redshanflora.redshanflora_backend.service.impl;

import com.redshanflora.redshanflora_backend.dto.employee.AssignedTaskDTO;
import com.redshanflora.redshanflora_backend.dto.employee.EmployeeOrderItemDTO;
import com.redshanflora.redshanflora_backend.entity.Order;
import com.redshanflora.redshanflora_backend.entity.OrderItem;
import com.redshanflora.redshanflora_backend.entity.OrderProcessing;
import com.redshanflora.redshanflora_backend.enums.MainOrderStatus;
import com.redshanflora.redshanflora_backend.enums.SubStatus;
import com.redshanflora.redshanflora_backend.repository.OrderItemRepository;
import com.redshanflora.redshanflora_backend.repository.OrderProcessingRepository;
import com.redshanflora.redshanflora_backend.repository.OrderRepository;
import com.redshanflora.redshanflora_backend.service.EmployeeTaskService;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.redshanflora.redshanflora_backend.dto.employee.StockCheckResponseDTO;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EmployeeTaskServiceImpl implements EmployeeTaskService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final OrderProcessingRepository orderProcessingRepository;

    /**
     * ===========================================
     * Assigned Task Table
     * ===========================================
     */
    @Override
    public List<AssignedTaskDTO> getAssignedTasks(
            Long employeeId
    ) {


        /*
         * Step 1:
         * Find all orders assigned to this employee
         */

        List<Order> orders =
                orderRepository.findByEmployeeId(
                        employeeId
                );


        List<AssignedTaskDTO> response =
                new ArrayList<>();


        /*
         * Step 2:
         * Calculate order_item values
         * for each order
         */

        for (Order order : orders) {


            Long orderId =
                    order.getId();


            /*
             * Count number of order_item rows
             */

            Long numberOfItems =
                    orderItemRepository
                            .countItemsByOrderId(
                                    orderId
                            );


            /*
             * Sum all quantities
             */

            Long totalQuantity =
                    orderItemRepository
                            .sumQuantityByOrderId(
                                    orderId
                            );


            /*
             * Create response DTO
             */

            AssignedTaskDTO dto =
                    AssignedTaskDTO.builder()

                            .orderId(orderId)

                            .numberOfItems(
                                    numberOfItems
                            )

                            .totalQuantity(
                                    totalQuantity
                            )

                            .build();


            response.add(dto);

        }


        return response;

    }

    /**
     * ===========================================
     * Item Details
     * ===========================================
     */
    @Override
    public List<EmployeeOrderItemDTO> getOrderItems(Long orderId) {

        List<OrderItem> items = orderItemRepository.findByOrderId(orderId);

        OrderProcessing processing = orderProcessingRepository
                .findByOrderId(orderId)
                .orElseThrow(() ->
                        new RuntimeException("Order Processing Not Found"));

        List<EmployeeOrderItemDTO> response = new ArrayList<>();

        for (OrderItem item : items) {

            EmployeeOrderItemDTO dto = EmployeeOrderItemDTO.builder()

                    .itemId(item.getId())

                    .imageUrl(item.getProduct().getImageUrl())

                    .itemName(item.getProduct().getProductName())

                    .quantity(item.getQuantity())

                    .stockQuantity(item.getProduct().getStockQuantity())

                    .status(processing.getSubStatus().name())

                    .build();

            response.add(dto);

        }

        return response;

    }

    @Override
    @Transactional(readOnly = true)
    public StockCheckResponseDTO checkStock(Long orderItemId) {

        OrderItem orderItem = orderItemRepository.findById(orderItemId)
                .orElseThrow(() ->
                        new RuntimeException("Order item not found"));

        if (orderItem.getProduct() == null) {

            throw new RuntimeException(
                    "This order item does not have a product"
            );
        }

        Integer requiredQuantity = orderItem.getQuantity();

        Integer availableStock = orderItem.getProduct().getStockQuantity();

        boolean stockAvailable =
                availableStock >= requiredQuantity;

        String message;

        if (stockAvailable) {

            message = "Stock is available";

        } else {

            message = "Insufficient stock";
        }

        return StockCheckResponseDTO.builder()

                .itemId(orderItem.getId())

                .itemName(
                        orderItem.getProduct().getProductName()
                )

                .requiredQuantity(requiredQuantity)

                .availableStock(availableStock)

                .stockAvailable(stockAvailable)

                .message(message)

                .build();
    }

    /**
     * ===========================================
     * Part 2
     * (Temporary)
     * ===========================================
     */

    @Override
    @Transactional
    public String startOrder(Long orderId) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        List<OrderItem> items = orderItemRepository.findByOrderId(orderId);

        for (OrderItem item : items) {

            if (item.getProduct().getStockQuantity() < item.getQuantity()) {
                return "Insufficient stock for "
                        + item.getProduct().getProductName();
            }
        }

        OrderProcessing processing = orderProcessingRepository
                .findByOrderId(orderId)
                .orElseThrow(() ->
                        new RuntimeException("Processing details not found"));

        // Order table
        order.setOrderStatus(MainOrderStatus.PROCESSING);
        order.setWorkingStatus("Active");

        // Order Processing table
        processing.setMainStatus(MainOrderStatus.PROCESSING);
        processing.setSubStatus(SubStatus.START);

        orderRepository.save(order);
        orderProcessingRepository.save(processing);

        return "Order started successfully.";
    }

    @Override
    @Transactional
    public String stopOrder(Long orderId) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        OrderProcessing processing = orderProcessingRepository
                .findByOrderId(orderId)
                .orElseThrow(() ->
                        new RuntimeException("Processing details not found"));

        // Order table
        order.setOrderStatus(MainOrderStatus.PROCESSING);
        order.setWorkingStatus("Offline");

        // Order Processing table
        processing.setMainStatus(MainOrderStatus.PROCESSING);
        processing.setSubStatus(SubStatus.STOP);

        orderRepository.save(order);
        orderProcessingRepository.save(processing);

        return "Order stopped successfully.";
    }

    @Override
    @Transactional
    public String resumeOrder(Long orderId) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        OrderProcessing processing = orderProcessingRepository
                .findByOrderId(orderId)
                .orElseThrow(() ->
                        new RuntimeException("Processing details not found"));

        // Order table
        order.setOrderStatus(MainOrderStatus.PROCESSING);
        order.setWorkingStatus("Active");

        // Order Processing table
        processing.setMainStatus(MainOrderStatus.PROCESSING);
        processing.setSubStatus(SubStatus.START);

        orderRepository.save(order);
        orderProcessingRepository.save(processing);

        return "Order resumed successfully.";
    }

    @Override
    @Transactional
    public String completeOrder(Long orderId) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        OrderProcessing processing = orderProcessingRepository
                .findByOrderId(orderId)
                .orElseThrow(() ->
                        new RuntimeException("Processing details not found"));

        // OrderProcessing table
        processing.setSubStatus(SubStatus.COMPLETED);
        processing.setMainStatus(MainOrderStatus.ORDER_COMPLETED);

        // Order table
        order.setOrderStatus(MainOrderStatus.ORDER_COMPLETED);
        order.setWorkingStatus("Offline");

        orderProcessingRepository.save(processing);
        orderRepository.save(order);

        return "Order completed successfully.";

    }
}
