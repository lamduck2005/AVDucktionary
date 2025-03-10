package com.example.avdict.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 50)
    private String email; // Email đăng nhập, duy nhất

    @Column(nullable = false, length = 255)
    private String password; // Mật khẩu đã mã hóa

    @Column(nullable = false, columnDefinition = "NVARCHAR(50)")
    private String fullName; // Họ và tên

    @Column(nullable = false, length = 10)
    private String role = "USER"; // Vai trò (USER hoặc ADMIN)

    @Column(length = 255)
    private String avatarUrl; // Ảnh đại diện (có thể null)

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now(); // Ngày tạo tài khoản

    @Column(nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now(); // Ngày cập nhật tài khoản

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
