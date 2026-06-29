package com.redshanflora.redshanflora_backend.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "sub_category", uniqueConstraints = {@UniqueConstraint(columnNames = {"category_id", "sub_category_name"})})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sub_category_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Column(name = "sub_category_name", nullable = false, length = 100)
    private String subCategoryName;
}
