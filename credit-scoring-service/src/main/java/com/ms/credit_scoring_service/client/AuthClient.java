package com.ms.credit_scoring_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

@FeignClient(name = "user-service")
public interface AuthClient {

    @PostMapping("/users/authenticate")
    String login(@RequestBody Map<String, String> request);
}
