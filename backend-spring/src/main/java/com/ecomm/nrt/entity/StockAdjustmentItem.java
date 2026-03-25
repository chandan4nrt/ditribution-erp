package com.ecomm.nrt.entity;

import com.ecomm.nrt.master.entity.ProductMaster;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "stock_adjustment_items")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class StockAdjustmentItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "stock_adjustment_id")
    private StockAdjustment stockAdjustment;

    @ManyToOne(optional = false)
    @JoinColumn(name = "product_id")
    private ProductMaster product;

    /** Positive for adding stock, Negative for deducting stock (damage, etc.) */
    @Column(nullable = false)
    private Integer changeQuantity;

    private String reason;
}
