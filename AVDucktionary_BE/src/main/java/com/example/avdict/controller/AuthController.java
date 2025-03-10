package com.example.avdict.controller;

import com.example.avdict.dto.AuthRequest;
import com.example.avdict.entity.User;
import com.example.avdict.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/auth") // Định nghĩa route API cho xác thực
@CrossOrigin(origins = "*") // Cho phép truy cập từ frontend
public class AuthController {

    private final UserRepository userRepository;

    public AuthController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // ✅ API ĐĂNG KÝ
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody AuthRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body("Email đã tồn tại!");
        }

        User newUser = new User();
        newUser.setEmail(request.getEmail());
        newUser.setPassword(request.getPassword()); // Chưa mã hóa (mã hóa sau)
        newUser.setFullName(request.getFullName());
        newUser.setAvatarUrl("");

        userRepository.save(newUser);
        return ResponseEntity.ok("Đăng ký thành công!");
    }

    // ✅ API ĐĂNG NHẬP
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest request) {
        Optional<User> user = userRepository.findByEmail(request.getEmail());

        if (user.isPresent() && user.get().getPassword().equals(request.getPassword())) {
            return ResponseEntity.ok(user.get());
        } else {
            return ResponseEntity.badRequest().body("Sai email hoặc mật khẩu!");
        }
    }
}
