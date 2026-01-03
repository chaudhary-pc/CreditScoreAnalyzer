package com.ms.credit_scoring_service.config;

import com.ms.credit_scoring_service.service.SystemTokenService;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Component
public class FeignClientInterceptor implements RequestInterceptor {

    private static final String AUTHORIZATION_HEADER = "Authorization";

    @Autowired
    private SystemTokenService systemTokenService;

    @Override
    public void apply(RequestTemplate template) {
        // 1. Try to get token from current HTTP Request (User initiated)
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            String authHeader = request.getHeader(AUTHORIZATION_HEADER);
            if (authHeader != null) {
                template.header(AUTHORIZATION_HEADER, authHeader);
                return;
            }
        }

        // 2. Fallback: Use System Token (Background Task / Kafka) System Token Fallback Approach
        String systemToken = systemTokenService.getSystemToken();
        if (systemToken != null) {
            template.header(AUTHORIZATION_HEADER, "Bearer " + systemToken);
        }
    }
}
