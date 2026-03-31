package com.itti.leadcapturing.repo;

import com.itti.leadcapturing.model.EvaluationCriterion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EvaluationCriterionRepository extends JpaRepository<EvaluationCriterion, Long> {

    @Query("SELECT c FROM EvaluationCriterion c WHERE c.isActive = true ORDER BY c.displayOrder ASC")
    List<EvaluationCriterion> findAllActive();

    @Query("SELECT c FROM EvaluationCriterion c ORDER BY c.displayOrder ASC")
    List<EvaluationCriterion> findAllOrderByDisplayOrder();
}