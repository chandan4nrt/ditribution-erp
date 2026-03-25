package com.ecomm.nrt.master.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Enhanced Product master — replaces the basic Product entity.
 * Mapped to the same "products" table so existing data is preserved.
 */
@Entity
@Table(name = "products")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ProductMaster {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 30)
    private String code;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String category;

    /** e.g. KG, PCS, LTR, BOX */
    @Column(length = 20)
    private String unit;

    /** HSN code for GST */
    @Column(length = 10)
    private String hsnCode;

    /** GST rate in % (e.g. 18.0) */
    @Column(precision = 5, scale = 2)
    private BigDecimal gstRate;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal basePrice;

    @Column(precision = 15, scale = 2)
    private BigDecimal discountPrice;

    /** Minimum stock level before reorder alert */
    private Integer reorderLevel;

    /** Current stock — managed by Inventory module */
    @Column(nullable = false)
    @Builder.Default
    private Integer currentStock = 0;

    @Builder.Default
    @Column(nullable = false)
    private Boolean isActive = true;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
