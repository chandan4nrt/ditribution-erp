package com.ecomm.nrt.repository;

import com.ecomm.nrt.entity.SalesOrder;
import com.ecomm.nrt.entity.SalesStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SalesOrderRepository extends JpaRepository<SalesOrder, Long> {
    Optional<SalesOrder> findBySoNumber(String soNumber);
    List<SalesOrder> findByStatus(SalesStatus status);
    List<SalesOrder> findByCustomerId(Long customerId);
}
