package com.ecomm.nrt.entity;

import com.ecomm.nrt.master.entity.ProductMaster;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "grn_items")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class GRNItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "grn_id")
    private GoodsReceivedNote grn;

    @ManyToOne(optional = false)
    @JoinColumn(name = "product_id")
    private ProductMaster product;

    /** Quantity received in this specific GRN entry */
    @Column(nullable = false)
    private Integer receivedQuantity;

    private String remarks;
}
