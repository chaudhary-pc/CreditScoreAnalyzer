package com.ms.credit_scoring_service.client;

import com.ms.credit_scoring_service.dto.FinancialDataDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "data-collection-service")
public interface DataCollectionClient {

    @GetMapping("/data/{userId}")
    List<FinancialDataDto> getAllDataForUser(@PathVariable("userId") Long userId);
}
