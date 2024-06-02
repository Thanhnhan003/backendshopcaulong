package com.nguyenthanhnhan.backendshopcaulong.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse {
    private UUID userId;
    private String fullname;
    private String email;
    private String roles;
    private String avatar;

    private LocalDateTime createdAt;
}
