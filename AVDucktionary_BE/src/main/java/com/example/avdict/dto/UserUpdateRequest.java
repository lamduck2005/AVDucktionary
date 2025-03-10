package com.example.avdict.dto;

import lombok.Data;

@Data
public class UserUpdateRequest {
    private String email;
    private String fullName;  // Dùng khi cập nhật thông tin
    private String avatarUrl; // Dùng khi cập nhật ảnh đại diện

    private String oldPassword; // Dùng khi đổi mật khẩu
    private String newPassword; // Dùng khi đổi mật khẩu
}
