package com.ecomm.nrt.entity;

import com.ecomm.nrt.master.entity.ProductMaster;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "stock_transfer_items")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class StockTransferItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "stock_transfer_id")
    private StockTransfer stockTransfer;

    @ManyToOne(optional = false)
    @JoinColumn(name = "product_id")
    private ProductMaster product;

    @Column(nullable = false)
    private Integer quantity;

    private String remarks;
}
