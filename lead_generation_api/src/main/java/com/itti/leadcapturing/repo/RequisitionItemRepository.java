package com.itti.leadcapturing.repo;

import com.itti.leadcapturing.model.RequisitionItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface RequisitionItemRepository extends JpaRepository<RequisitionItem, Long> {

    @Query("SELECT ri FROM RequisitionItem ri WHERE ri.requisition.id = :requisitionId")
    List<RequisitionItem> findByRequisitionId(@Param("requisitionId") Long requisitionId);

    @Query("SELECT ri FROM RequisitionItem ri WHERE ri.id = :id")
    Optional<RequisitionItem> findById(@Param("id") Long id);

    @Query("SELECT CASE WHEN COUNT(ri) > 0 THEN true ELSE false END FROM RequisitionItem ri WHERE ri.id = :id")
    boolean existsById(@Param("id") Long id);
}