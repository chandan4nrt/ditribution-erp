package com.ecomm.nrt.entity;

import com.ecomm.nrt.master.entity.ProductMaster;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "sales_order_items")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class SalesOrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "sales_order_id")
    private SalesOrder salesOrder;

    @ManyToOne(optional = false)
    @JoinColumn(name = "product_id")
    private ProductMaster product;

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal unitPrice;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal lineTotal;

    @Column(precision = 5, scale = 2)
    private BigDecimal taxRate;

    @Column(precision = 15, scale = 2)
    private BigDecimal taxAmount;

    @Column(columnDefinition = "TEXT")
    private String remarks;
}
