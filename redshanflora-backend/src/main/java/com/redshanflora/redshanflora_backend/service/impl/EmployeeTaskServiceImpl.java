package com.redshanflora.redshanflora_backend.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.redshanflora.redshanflora_backend.dto.employee.AssignedTaskDTO;
import com.redshanflora.redshanflora_backend.dto.employee.CustomizedOrderItemDTO;
import com.redshanflora.redshanflora_backend.dto.employee.EmployeeOrderItemDTO;
import com.redshanflora.redshanflora_backend.dto.employee.StockCheckResponseDTO;

import com.redshanflora.redshanflora_backend.entity.CustomizedBouquet;
import com.redshanflora.redshanflora_backend.entity.Employee;
import com.redshanflora.redshanflora_backend.entity.Order;
import com.redshanflora.redshanflora_backend.entity.OrderItem;
import com.redshanflora.redshanflora_backend.entity.OrderProcessing;

import com.redshanflora.redshanflora_backend.enums.MainOrderStatus;
import com.redshanflora.redshanflora_backend.enums.SubStatus;

import com.redshanflora.redshanflora_backend.repository.CustomizedBouquetRepository;
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
public class EmployeeTaskServiceImpl implements EmployeeTaskService {

    private final OrderRepository orderRepository;

    private final OrderItemRepository orderItemRepository;

    private final OrderProcessingRepository orderProcessingRepository;

    private final EmployeeRepository employeeRepository;

    private final CustomizedBouquetRepository customizedBouquetRepository;

    private final ObjectMapper objectMapper;


    // =========================================================
    // NORMAL ASSIGNED TASKS
    // =========================================================

    @Override
    @Transactional(readOnly = true)
    public List<AssignedTaskDTO> getNormalAssignedTasks(
            Long employeeId
    ) {

        List<Order> orders =
                orderRepository.findNormalAssignedOrders(
                        employeeId,
                        MainOrderStatus.ORDER_COMPLETED
                );

        return convertOrdersToTaskDTO(orders);
    }


    // =========================================================
    // CUSTOMIZED ASSIGNED TASKS
    // =========================================================

    @Override
    @Transactional(readOnly = true)
    public List<AssignedTaskDTO> getCustomizedAssignedTasks(
            Long employeeId
    ) {

        List<Order> orders =
                orderRepository.findCustomizedAssignedOrders(
                        employeeId,
                        MainOrderStatus.ORDER_COMPLETED
                );

        return convertCustomizedOrdersToTaskDTO(
                orders
        );
    }


    // =========================================================
    // NORMAL ORDER DTO CONVERSION
    // =========================================================

    private List<AssignedTaskDTO> convertOrdersToTaskDTO(
            List<Order> orders
    ) {

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


    // =========================================================
    // CUSTOMIZED ORDER DTO CONVERSION
    // =========================================================

    private List<AssignedTaskDTO> convertCustomizedOrdersToTaskDTO(
            List<Order> orders
    ) {

        List<AssignedTaskDTO> response =
                new ArrayList<>();

        for (Order order : orders) {

            Long orderId = order.getId();

            CustomizedBouquet customizedBouquet =
                    customizedBouquetRepository
                            .findByOrder(order)
                            .orElse(null);

            /*
             * If customized bouquet does not exist,
             * return empty customized item list.
             */
            if (customizedBouquet == null) {

                response.add(
                        AssignedTaskDTO.builder()
                                .orderId(orderId)
                                .numberOfItems(0L)
                                .totalQuantity(0L)
                                .items(new ArrayList<>())
                                .build()
                );

                continue;
            }


            /*
             * Get current customized item status.
             *
             * Since one CustomizedBouquet represents
             * one customized order item, the OrderProcessing
             * subStatus is enough to represent its working status.
             */
            String currentStatus =
                    getCustomizedItemStatus(order);


            CustomizedOrderItemDTO customizedItem =
                    buildCustomizedItemDTO(
                            order,
                            customizedBouquet,
                            currentStatus
                    );


            Long totalQuantity =
                    customizedItem.getQuantity() != null
                            ? customizedItem
                            .getQuantity()
                            .longValue()
                            : 0L;


            List<CustomizedOrderItemDTO> items =
                    new ArrayList<>();

            items.add(customizedItem);


            AssignedTaskDTO dto =
                    AssignedTaskDTO.builder()
                            .orderId(orderId)
                            .numberOfItems(1L)
                            .totalQuantity(totalQuantity)
                            .items(items)
                            .build();

            response.add(dto);
        }

        return response;
    }


    // =========================================================
    // GET CUSTOMIZED ITEM STATUS
    // =========================================================

    private String getCustomizedItemStatus(
            Order order
    ) {

        /*
         * Completed order
         */
        if (order.getOrderStatus()
                == MainOrderStatus.ORDER_COMPLETED) {

            return SubStatus.COMPLETED.name();
        }


        OrderProcessing processing =
                orderProcessingRepository
                        .findByOrderId(order.getId())
                        .orElse(null);


        if (processing == null) {

            return SubStatus.PENDING.name();
        }


        SubStatus subStatus =
                processing.getSubStatus();


        if (subStatus == null) {

            return SubStatus.PENDING.name();
        }


        return subStatus.name();
    }


    // =========================================================
    // BUILD CUSTOMIZED ITEM DTO
    // =========================================================

    private CustomizedOrderItemDTO buildCustomizedItemDTO(
            Order order,
            CustomizedBouquet customizedBouquet,
            String itemStatus
    ) {

        String flowerType = "N/A";

        Integer numberOfFlowers = 0;

        String bouquetStyle =
                customizedBouquet.getBouquetStyle();


        String snapshot =
                customizedBouquet
                        .getCustomBouquetSnapshot();


        // =====================================================
        // READ SNAPSHOT
        // =====================================================

        if (snapshot != null
                && !snapshot.isBlank()) {

            try {

                JsonNode root =
                        objectMapper.readTree(snapshot);


                // =================================================
                // BOUQUET STYLE
                // =================================================

                if (root.hasNonNull("bouquetStyle")) {

                    String snapshotBouquetStyle =
                            root.path("bouquetStyle")
                                    .asText(null);

                    if (snapshotBouquetStyle != null
                            && !snapshotBouquetStyle.isBlank()) {

                        bouquetStyle =
                                snapshotBouquetStyle;
                    }
                }


                // =================================================
                // FLOWER SUMMARY
                // =================================================

                JsonNode flowerSummary =
                        root.path("flowerSummary");


                if (flowerSummary.isArray()
                        && flowerSummary.size() > 0) {

                    List<String> flowerNames =
                            new ArrayList<>();

                    int totalFlowers = 0;


                    for (JsonNode flower :
                            flowerSummary) {

                        String productName =
                                flower.path("productName")
                                        .asText(null);

                        int quantity =
                                flower.path("quantity")
                                        .asInt(0);


                        if (productName != null
                                && !productName.isBlank()
                                && !flowerNames.contains(
                                productName
                        )) {

                            flowerNames.add(
                                    productName
                            );
                        }


                        totalFlowers += quantity;
                    }


                    if (!flowerNames.isEmpty()) {

                        flowerType =
                                String.join(
                                        ", ",
                                        flowerNames
                                );
                    }


                    numberOfFlowers =
                            totalFlowers;
                }

            } catch (Exception e) {

                throw new RuntimeException(
                        "Failed to parse customized bouquet snapshot for order "
                                + order.getId(),
                        e
                );
            }
        }


        // =====================================================
        // FALLBACK FLOWER TYPE
        // =====================================================

        if ((flowerType == null
                || flowerType.isBlank()
                || flowerType.equals("N/A"))
                && customizedBouquet.getPremiumBlooms() != null) {

            flowerType =
                    customizedBouquet
                            .getPremiumBlooms();
        }


        // =====================================================
        // FALLBACK BOUQUET STYLE
        // =====================================================

        if (bouquetStyle == null
                || bouquetStyle.isBlank()) {

            bouquetStyle = "N/A";
        }


        // =====================================================
        // BUILD DTO
        // =====================================================

        return CustomizedOrderItemDTO.builder()

                /*
                 * =================================================
                 * VERY IMPORTANT
                 *
                 * Use customized_bouquet.custom_id
                 *
                 * DO NOT use:
                 *
                 * snapshot.itemId
                 * order_item.id
                 * "CUSTOM-" + orderId
                 * =================================================
                 */
                .itemId(
                        customizedBouquet.getId()
                )

                .itemName(
                        "Customized Bouquet"
                )

                .quantity(
                        numberOfFlowers
                )

                .status(
                        itemStatus != null
                                ? itemStatus
                                : SubStatus.PENDING.name()
                )

                .flowerType(
                        flowerType
                )

                .numberOfFlowers(
                        numberOfFlowers
                )

                .bouquetStyle(
                        bouquetStyle
                )

                .build();
    }


    // =========================================================
    // NORMAL ORDER ITEMS
    // =========================================================

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


    // =========================================================
    // FIND NORMAL ORDER ITEM
    // =========================================================

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


    // =========================================================
    // FIND CUSTOMIZED BOUQUET
    // =========================================================

    private CustomizedBouquet findCustomizedBouquet(
            Long orderId,
            Long customId
    ) {

        Order order =
                orderRepository
                        .findById(orderId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Order not found: "
                                                + orderId
                                )
                        );


        CustomizedBouquet customizedBouquet =
                customizedBouquetRepository
                        .findByOrder(order)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Customized bouquet not found for order "
                                                + orderId
                                )
                        );


        /*
         * customId must be customized_bouquet.custom_id
         */
        if (!customizedBouquet.getId()
                .equals(customId)) {

            throw new RuntimeException(
                    "Customized item "
                            + customId
                            + " does not belong to order "
                            + orderId
            );
        }


        return customizedBouquet;
    }


    // =========================================================
    // CHECK STOCK - NORMAL ITEMS ONLY
    // =========================================================

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


    // =========================================================
    // START ITEM
    // =========================================================

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


        /*
         * =====================================================
         * FIRST CHECK CUSTOMIZED ORDER
         * =====================================================
         */

        CustomizedBouquet customizedBouquet =
                customizedBouquetRepository
                        .findByOrder(order)
                        .orElse(null);


        if (customizedBouquet != null) {

            /*
             * itemId MUST be customized_bouquet.custom_id
             */
            if (!customizedBouquet.getId()
                    .equals(itemId)) {

                throw new RuntimeException(
                        "Customized item "
                                + itemId
                                + " does not belong to order "
                                + orderId
                );
            }


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

                orderProcessingRepository.save(
                        processing
                );
            }


            return "Customized item started successfully.";
        }


        /*
         * =====================================================
         * NORMAL ORDER
         * =====================================================
         */

        OrderItem item =
                findOrderItem(
                        orderId,
                        itemId
                );


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

            orderProcessingRepository.save(
                    processing
            );
        }


        return "Item started successfully.";
    }


    // =========================================================
    // STOP ITEM
    // =========================================================

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


        /*
         * =====================================================
         * CUSTOMIZED ORDER
         * =====================================================
         */

        CustomizedBouquet customizedBouquet =
                customizedBouquetRepository
                        .findByOrder(order)
                        .orElse(null);


        if (customizedBouquet != null) {

            if (!customizedBouquet.getId()
                    .equals(itemId)) {

                throw new RuntimeException(
                        "Customized item "
                                + itemId
                                + " does not belong to order "
                                + orderId
                );
            }


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

                orderProcessingRepository.save(
                        processing
                );
            }


            return "Customized item stopped successfully.";
        }


        /*
         * =====================================================
         * NORMAL ORDER
         * =====================================================
         */

        OrderItem item =
                findOrderItem(
                        orderId,
                        itemId
                );


        item.setItemStatus(
                SubStatus.STOP
        );

        orderItemRepository.save(item);


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

            orderProcessingRepository.save(
                    processing
            );
        }


        return "Item stopped successfully.";
    }


    // =========================================================
    // RESUME ITEM
    // =========================================================

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


        /*
         * =====================================================
         * CUSTOMIZED ORDER
         * =====================================================
         */

        CustomizedBouquet customizedBouquet =
                customizedBouquetRepository
                        .findByOrder(order)
                        .orElse(null);


        if (customizedBouquet != null) {

            if (!customizedBouquet.getId()
                    .equals(itemId)) {

                throw new RuntimeException(
                        "Customized item "
                                + itemId
                                + " does not belong to order "
                                + orderId
                );
            }


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

                orderProcessingRepository.save(
                        processing
                );
            }


            return "Customized item resumed successfully.";
        }


        /*
         * =====================================================
         * NORMAL ORDER
         * =====================================================
         */

        OrderItem item =
                findOrderItem(
                        orderId,
                        itemId
                );


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

            orderProcessingRepository.save(
                    processing
            );
        }


        return "Item resumed successfully.";
    }


    // =========================================================
    // COMPLETE ITEM
    // =========================================================

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


        /*
         * =====================================================
         * CUSTOMIZED ORDER
         * =====================================================
         */

        CustomizedBouquet customizedBouquet =
                customizedBouquetRepository
                        .findByOrder(order)
                        .orElse(null);


        if (customizedBouquet != null) {

            /*
             * itemId = customized_bouquet.custom_id
             */
            if (!customizedBouquet.getId()
                    .equals(itemId)) {

                throw new RuntimeException(
                        "Customized item "
                                + itemId
                                + " does not belong to order "
                                + orderId
                );
            }


            /*
             * There is only ONE customized item
             * in this customized order.
             *
             * Therefore completing this item
             * completes the whole order.
             */

            order.setOrderStatus(
                    MainOrderStatus.ORDER_COMPLETED
            );

            order.setWorkingStatus(
                    "Finished"
            );


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

                orderProcessingRepository.save(
                        processing
                );
            }


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


            return "Customized item completed successfully. "
                    + "Order completed successfully.";
        }


        /*
         * =====================================================
         * NORMAL ORDER
         * =====================================================
         */

        OrderItem selectedItem =
                findOrderItem(
                        orderId,
                        itemId
                );


        selectedItem.setItemStatus(
                SubStatus.COMPLETED
        );

        orderItemRepository.save(
                selectedItem
        );


        List<OrderItem> allItems =
                orderItemRepository
                        .findByOrderId(orderId);


        if (allItems.isEmpty()) {

            throw new RuntimeException(
                    "No items found for order "
                            + orderId
            );
        }


        boolean allItemsCompleted = true;


        for (OrderItem item : allItems) {

            if (item.getItemStatus()
                    != SubStatus.COMPLETED) {

                allItemsCompleted = false;

                break;
            }
        }


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

                orderProcessingRepository.save(
                        processing
                );
            }


            return "Item completed successfully. "
                    + "Other items are still incomplete.";
        }


        /*
         * =====================================================
         * ALL NORMAL ITEMS COMPLETED
         * =====================================================
         */

        order.setOrderStatus(
                MainOrderStatus.ORDER_COMPLETED
        );

        order.setWorkingStatus(
                "Finished"
        );


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

            orderProcessingRepository.save(
                    processing
            );
        }


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


    // =========================================================
    // NORMAL COMPLETED TASKS
    // =========================================================

    @Override
    @Transactional(readOnly = true)
    public List<AssignedTaskDTO> getNormalCompletedTasks(
            Long employeeId
    ) {

        List<Order> orders =
                orderRepository.findNormalCompletedOrders(
                        employeeId,
                        MainOrderStatus.ORDER_COMPLETED
                );

        return convertOrdersToTaskDTO(orders);
    }


    // =========================================================
    // CUSTOMIZED COMPLETED TASKS
    // =========================================================

    @Override
    @Transactional(readOnly = true)
    public List<AssignedTaskDTO> getCustomizedCompletedTasks(
            Long employeeId
    ) {

        List<Order> orders =
                orderRepository.findCustomizedCompletedOrders(
                        employeeId,
                        MainOrderStatus.ORDER_COMPLETED
                );

        return convertCustomizedOrdersToTaskDTO(
                orders
        );
    }

    @Override
    @Transactional
    public String startCustomizedItem(
            Long orderId,
            Long customId
    ) {

        Order order =
                orderRepository.findById(orderId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Order not found: " + orderId
                                )
                        );

        CustomizedBouquet customizedBouquet =
                customizedBouquetRepository.findById(customId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Customized bouquet not found: "
                                                + customId
                                )
                        );

        // Make sure custom bouquet belongs to this order
        if (customizedBouquet.getOrder() == null
                || !customizedBouquet.getOrder()
                .getId()
                .equals(orderId)) {

            throw new RuntimeException(
                    "Customized bouquet "
                            + customId
                            + " does not belong to order "
                            + orderId
            );
        }

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

            orderProcessingRepository.save(
                    processing
            );
        }

        return "Customized item started successfully.";
    }

    @Override
    @Transactional
    public String stopCustomizedItem(
            Long orderId,
            Long customId
    ) {

        Order order =
                orderRepository.findById(orderId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Order not found: " + orderId
                                )
                        );

        CustomizedBouquet customizedBouquet =
                customizedBouquetRepository.findById(customId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Customized bouquet not found: "
                                                + customId
                                )
                        );

        if (customizedBouquet.getOrder() == null
                || !customizedBouquet.getOrder()
                .getId()
                .equals(orderId)) {

            throw new RuntimeException(
                    "Customized bouquet "
                            + customId
                            + " does not belong to order "
                            + orderId
            );
        }

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

            orderProcessingRepository.save(
                    processing
            );
        }

        return "Customized item stopped successfully.";
    }

    @Override
    @Transactional
    public String resumeCustomizedItem(
            Long orderId,
            Long customId
    ) {

        Order order =
                orderRepository.findById(orderId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Order not found: " + orderId
                                )
                        );

        CustomizedBouquet customizedBouquet =
                customizedBouquetRepository.findById(customId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Customized bouquet not found: "
                                                + customId
                                )
                        );

        if (customizedBouquet.getOrder() == null
                || !customizedBouquet.getOrder()
                .getId()
                .equals(orderId)) {

            throw new RuntimeException(
                    "Customized bouquet "
                            + customId
                            + " does not belong to order "
                            + orderId
            );
        }

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

            orderProcessingRepository.save(
                    processing
            );
        }

        return "Customized item resumed successfully.";
    }

    @Override
    @Transactional
    public String completeCustomizedItem(
            Long orderId,
            Long customId
    ) {

        Order order =
                orderRepository.findById(orderId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Order not found: " + orderId
                                )
                        );

        CustomizedBouquet customizedBouquet =
                customizedBouquetRepository.findById(customId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Customized bouquet not found: "
                                                + customId
                                )
                        );

        if (customizedBouquet.getOrder() == null
                || !customizedBouquet.getOrder()
                .getId()
                .equals(orderId)) {

            throw new RuntimeException(
                    "Customized bouquet "
                            + customId
                            + " does not belong to order "
                            + orderId
            );
        }


        // Customized order represents ONE customized item.
        // Therefore completing this custom item completes
        // the customized order.

        order.setOrderStatus(
                MainOrderStatus.ORDER_COMPLETED
        );

        order.setWorkingStatus(
                "Finished"
        );


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

            orderProcessingRepository.save(
                    processing
            );
        }


        Employee employee =
                order.getEmployee();

        if (employee != null) {

            employee.setStatus(
                    "Not Assigned"
            );

            employeeRepository.save(employee);
        }


        orderRepository.save(order);

        return "Customized item completed successfully.";
    }
}