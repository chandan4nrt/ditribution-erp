package com.ecomm.nrt.master.repository;

import com.ecomm.nrt.master.entity.ProductMaster;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductMasterRepository extends JpaRepository<ProductMaster, Long> {
    Optional<ProductMaster> findByCode(String code);
    boolean existsByCode(String code);

    @Query("SELECT p FROM ProductMaster p WHERE p.isActive = true AND " +
           "(LOWER(p.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(p.code) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(p.category) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<ProductMaster> searchActive(@Param("search") String search, Pageable pageable);

    Page<ProductMaster> findByIsActiveTrue(Pageable pageable);

    @Query("SELECT p FROM ProductMaster p WHERE p.isActive = true AND " +
           "p.reorderLevel IS NOT NULL AND p.currentStock <= p.reorderLevel")
    List<ProductMaster> findLowStockProducts();
}
