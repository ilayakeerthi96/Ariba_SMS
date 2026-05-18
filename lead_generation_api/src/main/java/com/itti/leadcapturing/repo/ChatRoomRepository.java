package com.itti.leadcapturing.repo;

import com.itti.leadcapturing.model.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    Optional<ChatRoom> findByRfqId(Long rfqId);

    Optional<ChatRoom> findByRfqIdAndIsActiveTrue(Long rfqId);

    List<ChatRoom> findByBuyerIdAndIsActiveTrue(Long buyerId);

    List<ChatRoom> findBySupplierIdAndIsActiveTrue(Long supplierId);

    boolean existsByRfqId(Long rfqId);
}
