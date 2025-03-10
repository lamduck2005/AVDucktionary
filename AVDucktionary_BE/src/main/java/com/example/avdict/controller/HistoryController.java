package com.example.avdict.controller;

import com.example.avdict.entity.History;
import com.example.avdict.entity.User;
import com.example.avdict.entity.Word;
import com.example.avdict.repository.HistoryRepository;
import com.example.avdict.repository.UserRepository;
import com.example.avdict.repository.WordRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/history")
public class HistoryController {

    @Autowired
    private HistoryRepository historyRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private WordRepository wordRepository;

    @GetMapping("/{userId}")
    public ResponseEntity<List<History>> getUserHistory(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "50") int limit) {

        List<History> history = historyRepository.findRecentHistory(userId, limit);
        return ResponseEntity.ok(history);
    }

    @DeleteMapping("/{userId}")
    @Transactional
    public ResponseEntity<String> removeAllHistory(@PathVariable Long userId) {
        Optional<User> user = userRepository.findById(userId);

        if (user.isPresent()) {
            List<History> histories = historyRepository.findByUser(user.get());
            historyRepository.deleteAll(histories);
            return ResponseEntity.ok("Đã xóa tất cả lịch sử!");
        }
        return ResponseEntity.badRequest().body("Lỗi khi xóa tất cả lịch sử.");
    }

    @PostMapping("/{userId}/{wordId}")
    public ResponseEntity<?> saveHistory(@PathVariable Long userId, @PathVariable Long wordId) {
        Optional<User> user = userRepository.findById(userId);
        Optional<Word> word = wordRepository.findById(wordId);

        if (user.isPresent() && word.isPresent()) {
            // Kiểm tra xem đã có lịch sử chưa, nếu có thì cập nhật thời gian
            Optional<History> existingHistory = historyRepository.findByUserAndWord(user.get(), word.get());
            if (existingHistory.isPresent()) {
                existingHistory.get().setViewedAt(LocalDateTime.now());
                historyRepository.save(existingHistory.get());
            } else {
                History history = new History(user.get(), word.get(), LocalDateTime.now());
                historyRepository.save(history);
            }
            return ResponseEntity.ok("Đã lưu vào lịch sử");
        }
        return ResponseEntity.badRequest().body("Lỗi: Người dùng hoặc từ không tồn tại!");
    }

    @DeleteMapping("/{userId}/{wordId}")
    @Transactional
    public ResponseEntity<String> removeHistory(@PathVariable Long userId, @PathVariable Long wordId) {
        Optional<User> user = userRepository.findById(userId);
        Optional<Word> word = wordRepository.findById(wordId);

        if (user.isPresent() && word.isPresent()) {
            historyRepository.deleteByUserAndWord(user.get(), word.get());
            return ResponseEntity.ok("Đã xóa lịch sử!");
        }
        return ResponseEntity.badRequest().body("Lỗi khi xóa lịch sử.");
    }
}
