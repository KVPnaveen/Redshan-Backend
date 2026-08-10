package com.redshanflora.redshanflora_backend.service.impl;

import com.redshanflora.redshanflora_backend.dto.employee.AssignedTaskDTO;
import com.redshanflora.redshanflora_backend.dto.employee.EmployeeOrderItemDTO;
import com.redshanflora.redshanflora_backend.dto.employee.StockCheckResponseDTO;
import com.redshanflora.redshanflora_backend.entity.Employee;
import com.redshanflora.redshanflora_backend.entity.Order;
import com.redshanflora.redshanflora_backend.entity.OrderItem;
import com.redshanflora.redshanflora_backend.entity.OrderProcessing;
import com.redshanflora.redshanflora_backend.enums.MainOrderStatus;
import com.redshanflora.redshanflora_backend.enums.SubStatus;
import com.redshanflora.redshanflora_backend.repository.EmployeeRepository;
import com.redshanflora.redshanflora_backend.repository.OrderItemRepository;
import com.redshanflora.redshanflora_backend.repository.OrderProcessingRepository;
import com.redshanflora.redshanflora_backend.repository.OrderRepository;
import com.redshanflora.redshanflora_backend.service.EmployeeTaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EmployeeTaskServiceImpl
        implements EmployeeTaskService {

    private final OrderRepository orderRepository;

    private final OrderItemRepository orderItemRepository;

    private final OrderProcessingRepository orderProcessingRepository;

    private final EmployeeRepository employeeRepository;


    /**
     * ============================================
     * Assigned Task Table
     * ============================================
     *
     * Only completely finished orders are removed.
     *
     * Example:
     *
     * Order 48
     * Item 40 = COMPLETED
     * Item 41 = PENDING
     *
     * Order 48 is STILL returned here.
     */
    @Override
    @Transactional(readOnly = true)
    public List<AssignedTaskDTO> getAssignedTasks(
            Long employeeId
    ) {

        List<Order> orders =
                orderRepository
                        .findByEmployeeIdAndOrderStatusNot(
                                employeeId,
                                MainOrderStatus.ORDER_COMPLETED
                        );

        List<AssignedTaskDTO> response =
                new ArrayList<>();

        for (Order order : orders) {

            Long orderId = order.getId();

            Long numberOfItems =
                    orderItemRepository
                            .countItemsByOrderId(orderId);

            Long totalQuantity =
                    orderItemRepository
                            .sumQuantityByOrderId(orderId);

            AssignedTaskDTO dto =
                    AssignedTaskDTO.builder()
                            .orderId(orderId)
                            .numberOfItems(
                                    numberOfItems != null
                                            ? numberOfItems
                                            : 0L
                            )
                            .totalQuantity(
                                    totalQuantity != null
                                            ? totalQuantity
                                            : 0L
                            )
                            .build();

            response.add(dto);
        }

        return response;
    }


    /**
     * ============================================
     * Get Item Details
     * ============================================
     */
    @Override
    @Transactional(readOnly = true)
    public List<EmployeeOrderItemDTO> getOrderItems(
            Long orderId
    ) {

        List<OrderItem> items =
                orderItemRepository
                        .findByOrderId(orderId);

        List<EmployeeOrderItemDTO> response =
                new ArrayList<>();

        for (OrderItem item : items) {

            EmployeeOrderItemDTO dto =
                    EmployeeOrderItemDTO.builder()

                            .itemId(
                                    item.getId()
                            )

                            .imageUrl(
                                    item.getProduct() != null
                                            ? item.getProduct()
                                            .getImageUrl()
                                            : null
                            )

                            .itemName(
                                    item.getProduct() != null
                                            ? item.getProduct()
                                            .getProductName()
                                            : null
                            )

                            .quantity(
                                    item.getQuantity()
                            )

                            .stockQuantity(
                                    item.getProduct() != null
                                            ? item.getProduct()
                                            .getStockQuantity()
                                            : 0
                            )

                            .status(
                                    item.getItemStatus() != null
                                            ? item.getItemStatus().name()
                                            : SubStatus.PENDING.name()
                            )

                            .build();

            response.add(dto);
        }

        return response;
    }


    /**
     * ============================================
     * Find ONE Item Belonging To ONE Order
     * ============================================
     *
     * We deliberately do NOT add a new repository
     * method here.
     *
     * We first get the items for the order and
     * then locate the requested item.
     *
     * This prevents:
     *
     * Order 48
     * Item 40
     * Item 41
     *
     * from accidentally updating both items.
     */
    private OrderItem findOrderItem(
            Long orderId,
            Long itemId
    ) {

        List<OrderItem> items =
                orderItemRepository
                        .findByOrderId(orderId);

        for (OrderItem item : items) {

            if (item.getId().equals(itemId)) {
                return item;
            }
        }

        throw new RuntimeException(
                "Order item "
                        + itemId
                        + " does not belong to order "
                        + orderId
        );
    }


    /**
     * ============================================
     * Check Stock
     * ============================================
     */
    @Override
    @Transactional(readOnly = true)
    public StockCheckResponseDTO checkStock(
            Long orderItemId
    ) {

        OrderItem orderItem =
                orderItemRepository
                        .findById(orderItemId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Order item not found"
                                )
                        );

        if (orderItem.getProduct() == null) {

            throw new RuntimeException(
                    "This order item does not have a product"
            );
        }

        Integer requiredQuantity =
                orderItem.getQuantity();

        Integer availableStock =
                orderItem
                        .getProduct()
                        .getStockQuantity();

        boolean stockAvailable =
                availableStock >= requiredQuantity;

        String message =
                stockAvailable
                        ? "Stock is available"
                        : "Insufficient stock";

        return StockCheckResponseDTO.builder()

                .itemId(
                        orderItem.getId()
                )

                .itemName(
                        orderItem
                                .getProduct()
                                .getProductName()
                )

                .requiredQuantity(
                        requiredQuantity
                )

                .availableStock(
                        availableStock
                )

                .stockAvailable(
                        stockAvailable
                )

                .message(
                        message
                )

                .build();
    }


    /**
     * ============================================
     * Start ONE Item
     * ============================================
     */
    @Override
    @Transactional
    public String startItem(
            Long orderId,
            Long itemId
    ) {

        Order order =
                orderRepository
                        .findById(orderId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Order not found"
                                )
                        );

        OrderItem item =
                findOrderItem(
                        orderId,
                        itemId
                );

        /**
         * Check stock ONLY for this item.
         */
        if (item.getProduct() != null) {

            Integer stock =
                    item.getProduct()
                            .getStockQuantity();

            Integer required =
                    item.getQuantity();

            if (stock < required) {

                return "Insufficient stock for "
                        + item.getProduct()
                        .getProductName();
            }
        }


        /**
         * Change ONLY this item.
         */
        item.setItemStatus(
                SubStatus.START
        );

        orderItemRepository.save(item);


        /**
         * Order remains processing.
         */
        order.setOrderStatus(
                MainOrderStatus.PROCESSING
        );

        order.setWorkingStatus(
                "Active"
        );

        orderRepository.save(order);


        /**
         * Update processing information.
         */
        OrderProcessing processing =
                orderProcessingRepository
                        .findByOrderId(orderId)
                        .orElse(null);

        if (processing != null) {

            processing.setMainStatus(
                    MainOrderStatus.PROCESSING
            );

            processing.setSubStatus(
                    SubStatus.START
            );

            orderProcessingRepository
                    .save(processing);
        }


        return "Item started successfully.";
    }


    /**
     * ============================================
     * Stop ONE Item
     * ============================================
     */
    @Override
    @Transactional
    public String stopItem(
            Long orderId,
            Long itemId
    ) {

        Order order =
                orderRepository
                        .findById(orderId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Order not found"
                                )
                        );

        OrderItem item =
                findOrderItem(
                        orderId,
                        itemId
                );


        /**
         * Change ONLY selected item.
         */
        item.setItemStatus(
                SubStatus.STOP
        );

        orderItemRepository.save(item);


        /**
         * Order is still not completed.
         */
        order.setOrderStatus(
                MainOrderStatus.PROCESSING
        );

        order.setWorkingStatus(
                "Offline"
        );

        orderRepository.save(order);


        OrderProcessing processing =
                orderProcessingRepository
                        .findByOrderId(orderId)
                        .orElse(null);

        if (processing != null) {

            processing.setMainStatus(
                    MainOrderStatus.PROCESSING
            );

            processing.setSubStatus(
                    SubStatus.STOP
            );

            orderProcessingRepository
                    .save(processing);
        }


        return "Item stopped successfully.";
    }


    /**
     * ============================================
     * Resume ONE Item
     * ============================================
     */
    @Override
    @Transactional
    public String resumeItem(
            Long orderId,
            Long itemId
    ) {

        Order order =
                orderRepository
                        .findById(orderId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Order not found"
                                )
                        );

        OrderItem item =
                findOrderItem(
                        orderId,
                        itemId
                );


        /**
         * Change ONLY selected item.
         */
        item.setItemStatus(
                SubStatus.START
        );

        orderItemRepository.save(item);


        order.setOrderStatus(
                MainOrderStatus.PROCESSING
        );

        order.setWorkingStatus(
                "Active"
        );

        orderRepository.save(order);


        OrderProcessing processing =
                orderProcessingRepository
                        .findByOrderId(orderId)
                        .orElse(null);

        if (processing != null) {

            processing.setMainStatus(
                    MainOrderStatus.PROCESSING
            );

            processing.setSubStatus(
                    SubStatus.START
            );

            orderProcessingRepository
                    .save(processing);
        }


        return "Item resumed successfully.";
    }


    /**
     * ============================================
     * COMPLETE ONE ITEM
     * ============================================
     *
     * THIS IS THE MOST IMPORTANT METHOD.
     *
     * Example:
     *
     * Order 48:
     *
     * Item 40 = PENDING
     * Item 41 = PENDING
     *
     * Complete item 40:
     *
     * Item 40 = COMPLETED
     * Item 41 = PENDING
     *
     * Order = PROCESSING
     *
     *
     * Then complete item 41:
     *
     * Item 40 = COMPLETED
     * Item 41 = COMPLETED
     *
     * Order = ORDER_COMPLETED
     */
    @Override
    @Transactional
    public String completeItem(
            Long orderId,
            Long itemId
    ) {

        Order order =
                orderRepository
                        .findById(orderId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Order not found"
                                )
                        );


        /**
         * Find ONLY the requested item.
         */
        OrderItem selectedItem =
                findOrderItem(
                        orderId,
                        itemId
                );


        /**
         * ========================================
         * STEP 1
         * Complete ONLY selected item.
         * ========================================
         */
        selectedItem.setItemStatus(
                SubStatus.COMPLETED
        );

        orderItemRepository.save(
                selectedItem
        );


        /**
         * ========================================
         * STEP 2
         * Get ALL items for this order.
         * ========================================
         */
        List<OrderItem> allItems =
                orderItemRepository
                        .findByOrderId(orderId);


        if (allItems.isEmpty()) {

            throw new RuntimeException(
                    "No items found for order "
                            + orderId
            );
        }


        /**
         * ========================================
         * STEP 3
         * Check whether EVERY item is completed.
         * ========================================
         */
        boolean allItemsCompleted = true;

        for (OrderItem item : allItems) {

            if (item.getItemStatus()
                    != SubStatus.COMPLETED) {

                allItemsCompleted = false;

                break;
            }
        }


        /**
         * ========================================
         * STEP 4A
         * Some items are still incomplete.
         *
         * IMPORTANT:
         * DO NOT complete the order.
         * ========================================
         */
        if (!allItemsCompleted) {

            order.setOrderStatus(
                    MainOrderStatus.PROCESSING
            );

            order.setWorkingStatus(
                    "Active"
            );

            orderRepository.save(order);


            OrderProcessing processing =
                    orderProcessingRepository
                            .findByOrderId(orderId)
                            .orElse(null);

            if (processing != null) {

                processing.setMainStatus(
                        MainOrderStatus.PROCESSING
                );

                processing.setSubStatus(
                        SubStatus.COMPLETED
                );

                orderProcessingRepository
                        .save(processing);
            }


            return "Item completed successfully. "
                    + "Other items are still incomplete.";
        }


        /**
         * ========================================
         * STEP 4B
         *
         * ALL items are completed.
         *
         * NOW the whole order can be completed.
         * ========================================
         */

        order.setOrderStatus(
                MainOrderStatus.ORDER_COMPLETED
        );

        order.setWorkingStatus(
                "Finished"
        );


        /**
         * Update OrderProcessing.
         */
        OrderProcessing processing =
                orderProcessingRepository
                        .findByOrderId(orderId)
                        .orElse(null);

        if (processing != null) {

            processing.setMainStatus(
                    MainOrderStatus.ORDER_COMPLETED
            );

            processing.setSubStatus(
                    SubStatus.COMPLETED
            );

            orderProcessingRepository
                    .save(processing);
        }


        /**
         * Make employee available again
         * ONLY when the entire order is finished.
         */
        Employee employee =
                order.getEmployee();

        if (employee != null) {

            employee.setStatus(
                    "Not Assigned"
            );

            employeeRepository.save(
                    employee
            );
        }


        orderRepository.save(order);


        return "All items completed. "
                + "Order completed successfully.";
    }


    /**
     * ============================================
     * Completed Task Table
     * ============================================
     *
     * Only orders with:
     *
     * ORDER_COMPLETED
     *
     * appear here.
     */
    @Override
    @Transactional(readOnly = true)
    public List<AssignedTaskDTO> getCompletedTasks(
            Long employeeId
    ) {

        List<Order> orders =
                orderRepository
                        .findByEmployeeIdAndOrderStatus(
                                employeeId,
                                MainOrderStatus.ORDER_COMPLETED
                        );

        List<AssignedTaskDTO> response =
                new ArrayList<>();

        for (Order order : orders) {

            Long orderId =
                    order.getId();

            Long numberOfItems =
                    orderItemRepository
                            .countItemsByOrderId(
                                    orderId
                            );

            Long totalQuantity =
                    orderItemRepository
                            .sumQuantityByOrderId(
                                    orderId
                            );

            AssignedTaskDTO dto =
                    AssignedTaskDTO.builder()

                            .orderId(
                                    orderId
                            )

                            .numberOfItems(
                                    numberOfItems != null
                                            ? numberOfItems
                                            : 0L
                            )

                            .totalQuantity(
                                    totalQuantity != null
                                            ? totalQuantity
                                            : 0L
                            )

                            .build();

            response.add(dto);
        }

        return response;
    }
}