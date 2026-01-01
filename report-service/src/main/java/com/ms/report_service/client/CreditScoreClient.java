package com.ms.report_service.client;

import com.ms.report_service.dto.CreditScoreDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "credit-scoring-service")
public interface CreditScoreClient {

    @GetMapping("/score/{userId}")
    CreditScoreDto getScoreByUserId(@PathVariable("userId") Long userId);
}
