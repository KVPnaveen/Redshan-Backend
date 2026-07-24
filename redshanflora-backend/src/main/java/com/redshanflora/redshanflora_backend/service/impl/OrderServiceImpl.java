package com.redshanflora.redshanflora_backend.service.impl;

import com.redshanflora.redshanflora_backend.dto.OrderListDto;
import com.redshanflora.redshanflora_backend.dto.order.OrderDetailsDto;
import com.redshanflora.redshanflora_backend.dto.order.OrderItemResponseDto;
import com.redshanflora.redshanflora_backend.dto.order.OrderSummaryDto;
import com.redshanflora.redshanflora_backend.dto.product.BouquetSnapshotDto;
import com.redshanflora.redshanflora_backend.entity.Customer;
import com.redshanflora.redshanflora_backend.entity.Order;
import com.redshanflora.redshanflora_backend.entity.OrderItem;
import com.redshanflora.redshanflora_backend.entity.Product;
import com.redshanflora.redshanflora_backend.entity.User;
import com.redshanflora.redshanflora_backend.exception.CheckoutValidationException;
import com.redshanflora.redshanflora_backend.repository.CustomerRepository;
import com.redshanflora.redshanflora_backend.repository.OrderItemRepository;
import com.redshanflora.redshanflora_backend.repository.OrderItemRepositoryNew;
import com.redshanflora.redshanflora_backend.repository.OrderRepository;
import com.redshanflora.redshanflora_backend.repository.UserRepository;
import com.redshanflora.redshanflora_backend.service.BouquetSnapshotService;
import com.redshanflora.redshanflora_backend.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;

    // Used by customer/admin order-detail functions
    private final OrderItemRepository orderItemRepository;

    // Used by the existing getAllOrders function
    private final OrderItemRepositoryNew orderItemRepositoryNew;

    private final UserRepository userRepository;
    private final CustomerRepository customerRepository;
    private final BouquetSnapshotService bouquetSnapshotService;

    /*
     * Existing team member function.
     */
    @Override
    public List<OrderListDto> getAllOrders() {

        List<Order> orders = orderRepository.findAll();
        List<OrderListDto> dtoList = new ArrayList<>();

        for (Order order : orders) {

            List<OrderItem> items = orderItemRepositoryNew.findByOrderId(order.getId());

            String itemName = "";

            if (!items.isEmpty()) {
                Product product = items.get(0).getProduct();

                if (product != null) {
                    itemName = product.getProductName();
                }
            }

            if ((itemName == null || itemName.isBlank())
                    && order.getCustomizedBouquet() != null) {
                itemName = "Customized Bouquet";
            }

            String type = order.getCustomizedBouquet() != null
                    ? "customize order"
                    : "not customize order";

            Customer customer = order.getCustomer();
            User user = customer != null ? customer.getUser() : null;

            OrderListDto dto = OrderListDto.builder()
                    .orderId(order.getId())
                    .customerName(user != null ? user.getName() : "Guest")
                    .itemName(itemName)
                    .price(order.getTotalAmount())
                    .type(type)
                    .build();

            dtoList.add(dto);
        }

        return dtoList;
    }

    /*
     * Finds the currently authenticated customer.
     */
    private Customer getAuthenticatedCustomer() {

        var authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null
                || !authentication.isAuthenticated()
                || authentication instanceof AnonymousAuthenticationToken) {

            throw new CheckoutValidationException(
                    "Access denied: User is not authenticated.");
        }

        String email = authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CheckoutValidationException(
                        "User not found: " + email));

        return customerRepository.findByUser(user)
                .orElseThrow(() -> new CheckoutValidationException(
                        "Customer profile not found for user: " + email));
    }

    @Override
    public List<OrderSummaryDto> getCustomerOrders() {

        Customer customer = getAuthenticatedCustomer();

        List<Order> orders = orderRepository.findByCustomerOrderByOrderDateDesc(customer);

        return orders.stream()
                .map(this::mapToSummaryDto)
                .collect(Collectors.toList());
    }

    @Override
    public OrderDetailsDto getCustomerOrderDetails(Long orderId) {

        Customer customer = getAuthenticatedCustomer();

        Order order = orderRepository.findByIdAndCustomer(orderId, customer)
                .orElseThrow(() -> new CheckoutValidationException(
                        "Order not found or access denied."));

        return mapToDetailsDto(order);
    }

    @Override
    public List<OrderSummaryDto> getAllOrdersForAdmin() {

        List<Order> orders = orderRepository.findAll(
                Sort.by(Sort.Direction.DESC, "orderDate"));

        return orders.stream()
                .map(this::mapToSummaryDto)
                .collect(Collectors.toList());
    }

    @Override
    public OrderDetailsDto getAdminOrderDetails(Long orderId) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new CheckoutValidationException(
                        "Order not found with ID: " + orderId));

        return mapToDetailsDto(order);
    }

    private OrderSummaryDto mapToSummaryDto(Order order) {

        return OrderSummaryDto.builder()
                .orderId(order.getId())
                .orderDate(order.getOrderDate())
                .totalAmount(order.getTotalAmount())
                .orderStatus(
                        order.getOrderStatus() != null
                                ? order.getOrderStatus().name()
                                : "PENDING")
                .paymentStatus(
                        order.getPayment() != null
                                ? order.getPayment().getPaymentStatus()
                                : "PENDING")
                .build();
    }

    private OrderDetailsDto mapToDetailsDto(Order order) {

        List<OrderItem> orderItems = orderItemRepository.findByOrder(order);

        List<OrderItemResponseDto> itemDtos = orderItems.stream()
                .map(item -> {

                    Product product = item.getProduct();

                    BigDecimal price = item.getPrice() != null
                            ? item.getPrice()
                            : BigDecimal.ZERO;

                    int quantity = item.getQuantity();

                    return OrderItemResponseDto.builder()
                            .productId(
                                    product != null ? product.getId() : null)
                            .productName(
                                    product != null
                                            ? product.getProductName()
                                            : "Unknown Product")
                            .imageUrl(
                                    product != null
                                            ? product.getImageUrl()
                                            : null)
                            .quantity(quantity)
                            .price(price)
                            .lineTotal(
                                    price.multiply(
                                            BigDecimal.valueOf(quantity)))
                            .build();
                })
                .collect(Collectors.toList());

        Customer customer = order.getCustomer();
        User user = customer != null ? customer.getUser() : null;

        OrderDetailsDto.OrderDetailsDtoBuilder builder = OrderDetailsDto.builder()
                .orderId(order.getId())
                .orderDate(order.getOrderDate())
                .totalAmount(order.getTotalAmount())
                .orderStatus(
                        order.getOrderStatus() != null
                                ? order.getOrderStatus().name()
                                : "PENDING")
                .paymentStatus(
                        order.getPayment() != null
                                ? order.getPayment()
                                        .getPaymentStatus()
                                : "PENDING")
                .customerName(
                        user != null ? user.getName() : "Guest")
                .customerEmail(
                        user != null ? user.getEmail() : "")
                .items(itemDtos);

        if (order.getCustomizedBouquet() != null) {

            String snapshotJson = order.getCustomizedBouquet()
                    .getCustomBouquetSnapshot();

            BouquetSnapshotDto snapshot = bouquetSnapshotService.deserializeSnapshot(
                    snapshotJson);

            if (snapshot != null
                    && (snapshot.getBouquetStyle() == null
                            || snapshot.getBouquetStyle().trim().isEmpty())) {

                snapshot.setBouquetStyle("ROUND");
            }

            builder.bouquetSnapshot(snapshot);
        }

        return builder.build();
    }
}