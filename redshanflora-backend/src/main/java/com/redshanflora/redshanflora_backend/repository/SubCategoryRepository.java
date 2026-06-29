package com.redshanflora.redshanflora_backend.repository;

import com.redshanflora.redshanflora_backend.entity.SubCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface SubCategoryRepository extends JpaRepository<SubCategory, Long> {
    List<SubCategory> findByCategory_Id(Long categoryId);
}
