package com.redshanflora.redshanflora_backend.service.impl;

import com.redshanflora.redshanflora_backend.dto.LoyaltyCheckoutResponse;
import com.redshanflora.redshanflora_backend.dto.LoyaltyResponse;
import com.redshanflora.redshanflora_backend.dto.RedeemRequest;
import com.redshanflora.redshanflora_backend.entity.Customer;
import com.redshanflora.redshanflora_backend.exception.LoyaltyException;
import com.redshanflora.redshanflora_backend.exception.ResourceNotFoundException;
import com.redshanflora.redshanflora_backend.repository.CustomerRepository;
import com.redshanflora.redshanflora_backend.repository.UserRepository;
import com.redshanflora.redshanflora_backend.service.LoyaltyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LoyaltyServiceImpl implements LoyaltyService {

    private final CustomerRepository customerRepository;
    private final UserRepository userRepository;

    @Transactional
    public Customer getOrCreateCustomer(Long userId) {
        Customer customer = customerRepository.findByUserId(userId).orElse(null);
        if (customer == null) {
            com.redshanflora.redshanflora_backend.entity.User user = userRepository.findById(userId)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
            customer = Customer.builder()
                    .user(user)
                    .address(user.getAddress() != null ? user.getAddress() : "Default Address")
                    .loyaltyPoints(0)
                    .build();
            customer = customerRepository.save(customer);
        }
        return customer;
    }

    @Override
    @Transactional
    public LoyaltyResponse getLoyaltyPointsByUserId(Long userId) {
        Customer customer = getOrCreateCustomer(userId);

        int pts = customer.getLoyaltyPoints() != null ? customer.getLoyaltyPoints() : 0;
        String membership;
        String nextLevel;
        int pointsToNext;

        if (pts >= 500) {
            membership = "Platinum";
            nextLevel = "Platinum";
            pointsToNext = 0;
        } else if (pts >= 300) {
            membership = "Gold";
            nextLevel = "Platinum";
            pointsToNext = 500 - pts;
        } else if (pts >= 100) {
            membership = "Silver";
            nextLevel = "Gold";
            pointsToNext = 300 - pts;
        } else {
            membership = "Bronze";
            nextLevel = "Silver";
            pointsToNext = 100 - pts;
        }

        return LoyaltyResponse.builder()
                .points(pts)
                .membership(membership)
                .nextLevel(nextLevel)
                .pointsToNext(pointsToNext)
                .build();
    }

    @Override
    @Transactional
    public LoyaltyCheckoutResponse getLoyaltyCheckoutDetails(Long userId) {
        Customer customer = getOrCreateCustomer(userId);

        int pts = customer.getLoyaltyPoints() != null ? customer.getLoyaltyPoints() : 0;
        String membership;
        int membershipDiscount;

        if (pts >= 500) {
            membership = "Platinum";
            membershipDiscount = 15;
        } else if (pts >= 300) {
            membership = "Gold";
            membershipDiscount = 10;
        } else if (pts >= 100) {
            membership = "Silver";
            membershipDiscount = 5;
        } else {
            membership = "Bronze";
            membershipDiscount = 0;
        }

        int redeemablePoints = (pts / 100) * 100;
        int redeemDiscount = redeemablePoints;
        int remainingPoints = pts - redeemablePoints;

        return LoyaltyCheckoutResponse.builder()
                .points(pts)
                .membership(membership)
                .membershipDiscount(membershipDiscount)
                .redeemablePoints(redeemablePoints)
                .redeemDiscount(redeemDiscount)
                .remainingPoints(remainingPoints)
                .build();
    }

    @Override
    @Transactional
    public LoyaltyResponse redeemPoints(RedeemRequest request) {
        Long userId = request.getUserId();
        Integer pointsToRedeem = request.getPoints();

        if (pointsToRedeem == null || pointsToRedeem <= 0 || pointsToRedeem % 100 != 0) {
            throw new LoyaltyException("Points to redeem must be a positive multiple of 100.");
        }

        Customer customer = getOrCreateCustomer(userId);

        int currentPoints = customer.getLoyaltyPoints() != null ? customer.getLoyaltyPoints() : 0;

        if (pointsToRedeem > currentPoints) {
            throw new LoyaltyException("Cannot redeem more points than customer has. Available: " + currentPoints);
        }

        customer.setLoyaltyPoints(currentPoints - pointsToRedeem);
        Customer updatedCustomer = customerRepository.save(customer);

        int pts = updatedCustomer.getLoyaltyPoints();
        String membership;
        String nextLevel;
        int pointsToNext;

        if (pts >= 500) {
            membership = "Platinum";
            nextLevel = "Platinum";
            pointsToNext = 0;
        } else if (pts >= 300) {
            membership = "Gold";
            nextLevel = "Platinum";
            pointsToNext = 500 - pts;
        } else if (pts >= 100) {
            membership = "Silver";
            nextLevel = "Gold";
            pointsToNext = 300 - pts;
        } else {
            membership = "Bronze";
            nextLevel = "Silver";
            pointsToNext = 100 - pts;
        }

        return LoyaltyResponse.builder()
                .points(pts)
                .membership(membership)
                .nextLevel(nextLevel)
                .pointsToNext(pointsToNext)
                .build();
    }
}
