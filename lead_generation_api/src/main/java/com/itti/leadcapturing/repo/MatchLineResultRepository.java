package com.itti.leadcapturing.repo;

import com.itti.leadcapturing.model.MatchLineResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MatchLineResultRepository extends JpaRepository<MatchLineResult, Long> {

    @Query("SELECT r FROM MatchLineResult r WHERE r.threeWayMatch.id = :matchId ORDER BY r.itemOrder ASC")
    List<MatchLineResult> findByMatchId(@Param("matchId") Long matchId);
}