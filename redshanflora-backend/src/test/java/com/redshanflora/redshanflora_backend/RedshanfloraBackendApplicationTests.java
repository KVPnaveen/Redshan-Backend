package com.redshanflora.redshanflora_backend;

import com.redshanflora.redshanflora_backend.controller.CheckoutController;
import com.redshanflora.redshanflora_backend.dto.cart.CartItemDto;
import com.redshanflora.redshanflora_backend.dto.payment.CheckoutRequest;
import com.redshanflora.redshanflora_backend.dto.payment.CheckoutResponse;
import com.redshanflora.redshanflora_backend.dto.wishlist.WishlistRequest;
import com.redshanflora.redshanflora_backend.dto.wishlist.WishlistResponse;
import com.redshanflora.redshanflora_backend.entity.Product;
import com.redshanflora.redshanflora_backend.enums.Role;
import com.redshanflora.redshanflora_backend.entity.User;
import com.redshanflora.redshanflora_backend.repository.ProductRepository;
import com.redshanflora.redshanflora_backend.repository.UserRepository;
import com.redshanflora.redshanflora_backend.repository.WishlistRepository;
import com.redshanflora.redshanflora_backend.service.WishlistService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class RedshanfloraBackendApplicationTests {

	@Autowired
	private WishlistService wishlistService;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private ProductRepository productRepository;

	@Autowired
	private WishlistRepository wishlistRepository;

	@Autowired
	private CheckoutController checkoutController;

	@Test
	void contextLoads() {
	}

	@Test
	void testAddToWishlist() {
		// 1. Create a dummy user
		User user = User.builder()
				.name("John Doe")
				.email("john.doe.wishlist@example.com")
				.password("password")
				.role(Role.CUSTOMER)
				.build();
		user = userRepository.save(user);

		// 2. Find any product seeded by DataSeeder
		Product product = productRepository.findAll().stream().findFirst().orElseThrow();

		// 3. Request adding to wishlist
		WishlistRequest request = WishlistRequest.builder()
				.userId(user.getId())
				.productId(product.getId())
				.build();

		WishlistResponse response = wishlistService.addToWishlist(request);

		// 4. Assert
		assertNotNull(response);
		assertNotNull(response.getWishlistId());
		assertEquals(product.getId(), response.getProductId());

		// 5. Verify it is saved in the database wishlist table
		boolean exists = wishlistRepository.existsByUser_IdAndProduct_Id(user.getId(), product.getId());
		assertTrue(exists);
	}

	@Test
	void testInitializeCheckout() {
		// 1. Get a product from the database
		Product product = productRepository.findAll().stream().findFirst().orElseThrow();

		// 2. Create a checkout request with this product
		CartItemDto cartItem = new CartItemDto();
		cartItem.setId(product.getId());
		cartItem.setTitle(product.getProductName());
		cartItem.setQuantity(2);
		cartItem.setIsCustom(false);
		cartItem.setNumericPrice(product.getPrice().doubleValue());

		CheckoutRequest request = new CheckoutRequest(
				Collections.singletonList(cartItem),
				"USD"
		);

		// 3. Call the checkout controller
		ResponseEntity<CheckoutResponse> responseEntity = checkoutController.initializeCheckout(request);

		// 4. Verify
		assertNotNull(responseEntity);
		assertEquals(200, responseEntity.getStatusCode().value());
		CheckoutResponse response = responseEntity.getBody();
		assertNotNull(response);
		assertNotNull(response.getHash());
		assertNotNull(response.getOrderId());
		assertEquals("1236591", response.getMerchantId());
		assertEquals(product.getPrice().doubleValue() * 2, response.getAmount());
	}

	@Test
	void testCheckoutNotificationAndStatus() {
		// 1. Initially check order status, should return SUCCESS due to localhost fallback mocking
		ResponseEntity<Map<String, String>> initialResponse = checkoutController.getPaymentStatus("ORDER-TEST-123");
		assertNotNull(initialResponse);
		assertEquals("SUCCESS", initialResponse.getBody().get("status"));

		// 2. Submit notification for success (status_code 2)
		String merchantIdVal = "1236591";
		String orderIdVal = "ORDER-TEST-456";
		String payhereAmountVal = "250.00";
		String payhereCurrencyVal = "LKR";
		int statusCodeVal = 2;
		String secretKeyVal = "NDE2NTgwODM2MTEwOTQ3Mzk3MDUyNjc0MTU2NzA0MzgzNDcwMTE3MA=="; // secret
		
		String hashedSecret;
		try {
			java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
			byte[] digest = md.digest(secretKeyVal.getBytes());
			StringBuilder sb = new StringBuilder();
			for (byte b : digest) {
				String hex = Integer.toHexString(0xff & b);
				if (hex.length() == 1) sb.append('0');
				sb.append(hex);
			}
			hashedSecret = sb.toString().toUpperCase();
			
			String hashStr = merchantIdVal + orderIdVal + payhereAmountVal + payhereCurrencyVal + statusCodeVal + hashedSecret;
			byte[] digest2 = md.digest(hashStr.getBytes());
			StringBuilder sb2 = new StringBuilder();
			for (byte b : digest2) {
				String hex = Integer.toHexString(0xff & b);
				if (hex.length() == 1) sb2.append('0');
				sb2.append(hex);
			}
			String md5sig = sb2.toString().toUpperCase();

			// Send successful notification
			checkoutController.receiveNotification(
					merchantIdVal, orderIdVal, "PAY-789", payhereAmountVal, payhereCurrencyVal, statusCodeVal, md5sig
			);

			// Query status, should return SUCCESS
			ResponseEntity<Map<String, String>> successResponse = checkoutController.getPaymentStatus(orderIdVal);
			assertEquals("SUCCESS", successResponse.getBody().get("status"));

			// Send failed notification (status_code -2)
			String orderIdValFail = "ORDER-TEST-789";
			int statusCodeFail = -2;
			String hashStrFail = merchantIdVal + orderIdValFail + payhereAmountVal + payhereCurrencyVal + statusCodeFail + hashedSecret;
			byte[] digestFail = md.digest(hashStrFail.getBytes());
			StringBuilder sbFail = new StringBuilder();
			for (byte b : digestFail) {
				String hex = Integer.toHexString(0xff & b);
				if (hex.length() == 1) sbFail.append('0');
				sbFail.append(hex);
			}
			String md5sigFail = sbFail.toString().toUpperCase();

			checkoutController.receiveNotification(
					merchantIdVal, orderIdValFail, "PAY-790", payhereAmountVal, payhereCurrencyVal, statusCodeFail, md5sigFail
			);

			// Query status, should return FAILED
			ResponseEntity<Map<String, String>> failResponse = checkoutController.getPaymentStatus(orderIdValFail);
			assertEquals("FAILED", failResponse.getBody().get("status"));

		} catch (Exception e) {
			fail(e);
		}
	}
}
