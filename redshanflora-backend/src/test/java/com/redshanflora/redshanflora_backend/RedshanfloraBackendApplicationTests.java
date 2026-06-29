package com.redshanflora.redshanflora_backend;

import com.redshanflora.redshanflora_backend.dto.WishlistRequest;
import com.redshanflora.redshanflora_backend.dto.WishlistResponse;
import com.redshanflora.redshanflora_backend.entity.Product;
import com.redshanflora.redshanflora_backend.entity.Role;
import com.redshanflora.redshanflora_backend.entity.User;
import com.redshanflora.redshanflora_backend.repository.ProductRepository;
import com.redshanflora.redshanflora_backend.repository.UserRepository;
import com.redshanflora.redshanflora_backend.repository.WishlistRepository;
import com.redshanflora.redshanflora_backend.service.WishlistService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

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

	@Test
	void contextLoads() {
	}

	@Test
	void testAddToWishlist() {
		// 1. Create a dummy user
		User user = User.builder()
				.firstName("John")
				.lastName("Doe")
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
}
