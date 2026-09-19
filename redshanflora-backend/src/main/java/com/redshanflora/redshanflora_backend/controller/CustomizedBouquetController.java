package com.redshanflora.redshanflora_backend.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.redshanflora.redshanflora_backend.config.WebConfig;
import com.redshanflora.redshanflora_backend.dto.customized.CustomizedBouquetUploadResponseDTO;
import com.redshanflora.redshanflora_backend.entity.CustomizedBouquet;
import com.redshanflora.redshanflora_backend.entity.Order;
import com.redshanflora.redshanflora_backend.repository.CustomizedBouquetRepository;
import com.redshanflora.redshanflora_backend.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;

@Slf4j
@RestController
@RequestMapping("/api/customized-bouquets")
@RequiredArgsConstructor
public class CustomizedBouquetController {

    private final OrderRepository orderRepository;
    private final CustomizedBouquetRepository customizedBouquetRepository;
    private final ObjectMapper objectMapper;

    private static final Set<String> ALLOWED_EXTENSIONS = Set.of("png", "jpg", "jpeg", "webp");

    @PostMapping(value = "/upload-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadCustomizedBouquetImage(
            @RequestParam("image") MultipartFile image,
            @RequestParam("customerId") Long customerId,
            @RequestParam("orderId") Long orderId) {

        // 1. Customer / Order validation
        log.info("[DEBUG CONTROLLER] Reached uploadCustomizedBouquetImage controller: customerId={}, orderId={}", customerId, orderId);
        var auth = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            log.info("[DEBUG CONTROLLER] Authenticated user: username={}, authorities={}", auth.getName(), auth.getAuthorities());
        } else {
            log.warn("[DEBUG CONTROLLER] Authentication context is NULL!");
        }

        if (customerId == null || orderId == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "customerId and orderId are required"));
        }

        Optional<Order> orderOptional = orderRepository.findById(orderId);
        if (orderOptional.isEmpty()) {
            log.warn("[DEBUG CONTROLLER] Order not found: orderId={}", orderId);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Order not found with ID: " + orderId));
        }

        Order order = orderOptional.get();
        Long actualCustomerTableId = order.getCustomer() != null ? order.getCustomer().getId() : null;
        Long actualUserTableId = (order.getCustomer() != null && order.getCustomer().getUser() != null)
                ? order.getCustomer().getUser().getId()
                : null;

        log.info("[DEBUG CONTROLLER] Order ownership check: orderId={}, requested customerId={}, actual order.customer.id={}, actual order.customer.user.id={}",
                orderId, customerId, actualCustomerTableId, actualUserTableId);

        boolean belongsToCustomer = (actualCustomerTableId != null && actualCustomerTableId.equals(customerId))
                || (actualUserTableId != null && actualUserTableId.equals(customerId));

        if (!belongsToCustomer) {
            log.warn("[DEBUG CONTROLLER] Forbidden: Order ID {} does not belong to Customer/User ID {}", orderId, customerId);
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "Order ID " + orderId + " does not belong to Customer ID " + customerId));
        }


        // 2. File Validation
        if (image == null || image.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Uploaded file cannot be empty"));
        }

        String originalFilename = image.getOriginalFilename();
        String fileExtension = getFileExtension(originalFilename);

        if (fileExtension == null || !ALLOWED_EXTENSIONS.contains(fileExtension.toLowerCase(Locale.ROOT))) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid image format. Allowed formats: png, jpg, jpeg, webp"));
        }

        String contentType = image.getContentType();
        if (contentType != null && !contentType.startsWith("image/")) {
            return ResponseEntity.badRequest().body(Map.of("error", "File MIME type must be an image"));
        }

        try {
            // 3. File Storage
            String ext = fileExtension.toLowerCase(Locale.ROOT);
            String uniqueUuid = UUID.randomUUID().toString();
            String fileName = String.format("customer_%d_order_%d_%s.%s", customerId, orderId, uniqueUuid, ext);

            Path uploadsBaseDir = WebConfig.resolveUploadDir();
            Path uploadDir = uploadsBaseDir.resolve("custom-bouquets").toAbsolutePath().normalize();
            log.info("[DEBUG CONTROLLER] Resolved bouquet upload directory: {}", uploadDir);

            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir);
            }

            Path targetPath = uploadDir.resolve(fileName).normalize();

            // Safety check against path traversal
            if (!targetPath.startsWith(uploadDir)) {
                return ResponseEntity.badRequest().body(Map.of("error", "Invalid file destination path"));
            }

            Files.copy(image.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
            log.info("[DEBUG CONTROLLER] Saved bouquet image to physical path: {}", targetPath);

            String relativePath = "uploads/custom-bouquets/" + fileName;
            String imageUrl = "/uploads/custom-bouquets/" + fileName;

            // Patch the customized_bouquet snapshot JSON to store imageUrl directly in DB.
            // This means the employee task lookup will read the URL from the snapshot
            // instead of trying to guess it by scanning filenames on disk.
            try {
                CustomizedBouquet bouquet = customizedBouquetRepository.findByOrder(order).orElse(null);
                if (bouquet != null) {
                    String existingSnapshot = bouquet.getCustomBouquetSnapshot();
                    ObjectNode snapshotNode;
                    if (existingSnapshot != null && !existingSnapshot.isBlank()) {
                        JsonNode parsed = objectMapper.readTree(existingSnapshot);
                        snapshotNode = (parsed instanceof ObjectNode) ? (ObjectNode) parsed : objectMapper.createObjectNode();
                    } else {
                        snapshotNode = objectMapper.createObjectNode();
                    }
                    snapshotNode.put("imageUrl", imageUrl);
                    bouquet.setCustomBouquetSnapshot(objectMapper.writeValueAsString(snapshotNode));
                    customizedBouquetRepository.save(bouquet);
                    log.info("[DEBUG CONTROLLER] Patched snapshot imageUrl for orderId={} -> {}", orderId, imageUrl);
                } else {
                    log.warn("[DEBUG CONTROLLER] No CustomizedBouquet found for orderId={}, imageUrl not saved to snapshot", orderId);
                }
            } catch (Exception snapshotEx) {
                // Non-fatal: image is already saved to disk; just log and continue
                log.warn("[DEBUG CONTROLLER] Failed to patch snapshot imageUrl for orderId={}: {}", orderId, snapshotEx.getMessage());
            }

            CustomizedBouquetUploadResponseDTO response = CustomizedBouquetUploadResponseDTO.builder()
                    .customerId(customerId)
                    .orderId(orderId)
                    .fileName(fileName)
                    .relativePath(relativePath)
                    .build();

            return ResponseEntity.ok(response);

        } catch (IOException e) {
            log.error("Failed to store custom bouquet image: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to store image: " + e.getMessage()));
        }
    }

    private String getFileExtension(String filename) {
        if (filename == null || filename.lastIndexOf('.') == -1) {
            return null;
        }
        return filename.substring(filename.lastIndexOf('.') + 1);
    }
}
