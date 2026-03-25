package com.ecomm.nrt.repository;

import com.ecomm.nrt.entity.PurchaseOrder;
import com.ecomm.nrt.entity.PurchaseStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface PurchaseOrderRepository extends JpaRepository<PurchaseOrder, Long> {

    Optional<PurchaseOrder> findByPoNumber(String poNumber);

    List<PurchaseOrder> findByStatus(PurchaseStatus status);

    List<PurchaseOrder> findBySupplierId(Long supplierId);

    @Query("SELECT po FROM PurchaseOrder po WHERE (:status IS NULL OR po.status = :status) " +
           "AND (:supplierId IS NULL OR po.supplier.id = :supplierId) " +
           "AND (:startDate IS NULL OR po.poDate >= :startDate) " +
           "AND (:endDate IS NULL OR po.poDate <= :endDate)")
    List<PurchaseOrder> searchPO(
            @Param("status") PurchaseStatus status,
            @Param("supplierId") Long supplierId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);
}
