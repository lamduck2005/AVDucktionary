package com.example.avdict.dto;

import lombok.Data;

@Data
public class AuthRequest {
    private String email;
    private String password;
    private String fullName; // Thêm họ và tên khi đăng ký hoặc cập nhật
}
