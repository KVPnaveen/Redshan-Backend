package com.redshanflora.redshanflora_backend.repository;

import com.redshanflora.redshanflora_backend.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByCategory_Id(Long categoryId);
    List<Product> findBySubCategory_Id(Long subCategoryId);
    List<Product> findByFeaturedTrue();
    List<Product> findByProductNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(String productNameKeyword, String descriptionKeyword);
}
