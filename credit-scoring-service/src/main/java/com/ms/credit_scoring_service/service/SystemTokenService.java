package com.ms.credit_scoring_service.service;

import com.ms.credit_scoring_service.client.AuthClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class SystemTokenService {

    private static final Logger logger = LogManager.getLogger(SystemTokenService.class);

    @Value("${SYSTEM_USERNAME:system}")
    private String systemUsername;

    @Value("${SYSTEM_PASSWORD:system-password-secure}")
    private String systemPassword;

    @Autowired
    private AuthClient authClient;

    private String cachedToken;

    public String getSystemToken() {
        if (cachedToken == null) {
            logger.info("System token is null. Authenticating as system user...");
            refreshSystemToken();
        }
        return cachedToken;
    }

    private void refreshSystemToken() {
        try {
            Map<String, String> loginRequest = new HashMap<>();
            loginRequest.put("username", systemUsername);
            loginRequest.put("password", systemPassword);

            cachedToken = authClient.login(loginRequest);
            logger.info("Successfully authenticated as system user.");
        } catch (Exception e) {
            logger.error("Failed to authenticate as system user", e);
            throw new RuntimeException("Could not obtain system token");
        }
    }
}
