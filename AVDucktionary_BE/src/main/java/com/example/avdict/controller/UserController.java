package com.example.avdict.controller;

import com.example.avdict.dto.UserUpdateRequest;
import com.example.avdict.entity.User;
import com.example.avdict.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/users") // Định nghĩa route API cho user
@CrossOrigin(origins = "*") // Cho phép truy cập từ frontend
public class UserController {

    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // ✅ API LẤY THÔNG TIN USER
    @GetMapping("/me")
    public ResponseEntity<?> getUserInfo(@RequestParam String email) {
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isEmpty()) {
            return ResponseEntity.badRequest().body("User không tồn tại!");
        }
        return user.map(ResponseEntity::ok).get();
    }

    // ✅ API CẬP NHẬT USER (Họ tên, avatar)
    @PutMapping("/update")
    public ResponseEntity<?> updateUser(@RequestBody UserUpdateRequest request) {
        Optional<User> userOptional = userRepository.findByEmail(request.getEmail());

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.setFullName(request.getFullName());
            user.setAvatarUrl(request.getAvatarUrl());
            userRepository.save(user);
            return ResponseEntity.ok("Cập nhật thông tin thành công!");
        } else {
            return ResponseEntity.badRequest().body("User không tồn tại!");
        }
    }

    // ✅ API ĐỔI MẬT KHẨU
    @PutMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody UserUpdateRequest request) {
        Optional<User> userOptional = userRepository.findByEmail(request.getEmail());

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            if (!user.getPassword().equals(request.getOldPassword())) {
                return ResponseEntity.badRequest().body("Mật khẩu cũ không chính xác!");
            }

            user.setPassword(request.getNewPassword());
            userRepository.save(user);
            return ResponseEntity.ok("Đổi mật khẩu thành công!");
        } else {
            return ResponseEntity.badRequest().body("User không tồn tại!");
        }
    }
}
