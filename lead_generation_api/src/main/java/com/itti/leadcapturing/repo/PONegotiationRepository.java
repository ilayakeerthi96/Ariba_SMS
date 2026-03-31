package com.itti.leadcapturing.repo;

import com.itti.leadcapturing.model.PONegotiation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PONegotiationRepository extends JpaRepository<PONegotiation, Long> {

    @Query("SELECT DISTINCT n FROM PONegotiation n " +
           "LEFT JOIN FETCH n.lineItems " +
           "WHERE n.id = :id")
    Optional<PONegotiation> findByIdWithLineItems(@Param("id") Long id);

    @Query("SELECT n FROM PONegotiation n WHERE n.rfq.id = :rfqId ORDER BY n.createdAt DESC")
    List<PONegotiation> findByRfqId(@Param("rfqId") Long rfqId);

    @Query("SELECT n FROM PONegotiation n WHERE n.rfq.id = :rfqId AND n.supplier.id = :supplierId ORDER BY n.createdAt DESC")
    Optional<PONegotiation> findLatestByRfqAndSupplier(
            @Param("rfqId") Long rfqId, @Param("supplierId") Long supplierId);

    @Query("SELECT n FROM PONegotiation n WHERE n.createdByUserId = :userId ORDER BY n.createdAt DESC")
    List<PONegotiation> findByCreatedByUserId(@Param("userId") Long userId);
}