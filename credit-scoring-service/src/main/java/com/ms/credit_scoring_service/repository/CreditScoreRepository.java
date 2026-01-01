package com.ms.credit_scoring_service.repository;

import com.ms.credit_scoring_service.entity.CreditScore;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CreditScoreRepository extends JpaRepository<CreditScore, Long> {
    Optional<CreditScore> findByUserId(Long userId);

    @Query("SELECT AVG(c.score) FROM CreditScore c")
    Double findAverageScore();
}
