package com.ms.data_collection_service.repository;

import com.ms.data_collection_service.entity.FinancialData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FinancialDataRepository extends JpaRepository<FinancialData, Long> {

    List<FinancialData> findByUserId(Long userId);

    List<FinancialData> findByUserIdAndDataType(Long userId, String dataType);
}
