package com.ecomm.nrt.repository;

import com.ecomm.nrt.entity.GoodsReceivedNote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GoodsReceivedNoteRepository extends JpaRepository<GoodsReceivedNote, Long> {
    Optional<GoodsReceivedNote> findByGrnNumber(String grnNumber);
    List<GoodsReceivedNote> findByPurchaseOrderId(Long purchaseOrderId);
}
