package com.ecomm.nrt.entity;

import com.ecomm.nrt.master.entity.ProductMaster;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "purchase_order_items")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class PurchaseOrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "purchase_order_id")
    private PurchaseOrder purchaseOrder;

    @ManyToOne(optional = false)
    @JoinColumn(name = "product_id")
    private ProductMaster product;

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal unitPrice;

    /** Total for this line (quantity * unitPrice) before tax */
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal lineTotal;

    /** Tax rate (%) applied to this line (usually from ProductMaster at time of creation) */
    @Column(precision = 5, scale = 2)
    private BigDecimal taxRate;

    /** Total tax for this line */
    @Column(precision = 15, scale = 2)
    private BigDecimal taxAmount;

    @Column(columnDefinition = "TEXT")
    private String remarks;
}
