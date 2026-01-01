package com.ms.user_service.dto;

import lombok.Data;

@Data
public class UserUpdateRequest {
    private String email;
    private String role;
    private String status;
}
