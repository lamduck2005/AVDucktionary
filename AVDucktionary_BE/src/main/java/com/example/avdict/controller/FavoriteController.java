package com.example.avdict.controller;

import com.example.avdict.entity.Favorite;
import com.example.avdict.entity.User;
import com.example.avdict.entity.Word;
import com.example.avdict.repository.FavoriteRepository;
import com.example.avdict.repository.UserRepository;
import com.example.avdict.repository.WordRepository;
import jakarta.transaction.Transactional;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/favorites")
@CrossOrigin(origins = "*")
public class FavoriteController {

    private final FavoriteRepository favoriteRepository;
    private final UserRepository userRepository;
    private final WordRepository wordRepository;

    public FavoriteController(FavoriteRepository favoriteRepository, UserRepository userRepository, WordRepository wordRepository) {
        this.favoriteRepository = favoriteRepository;
        this.userRepository = userRepository;
        this.wordRepository = wordRepository;
    }

    // ✅ API LẤY DANH SÁCH TỪ YÊU THÍCH
    @GetMapping("/{userId}")
    public ResponseEntity<List<Favorite>> getFavorites(@PathVariable Long userId) {
        Optional<User> user = userRepository.findById(userId);
        return user.map(value -> ResponseEntity.ok(favoriteRepository.findByUser(value)))
                .orElseGet(() -> ResponseEntity.badRequest().build());
    }

    // ✅ API THÊM TỪ YÊU THÍCH
    @PostMapping("/{userId}/{wordId}")
    public ResponseEntity<String> addFavorite(@PathVariable Long userId, @PathVariable Long wordId) {
        Optional<User> user = userRepository.findById(userId);
        Optional<Word> word = wordRepository.findById(wordId);

        if (user.isPresent() && word.isPresent()) {
            Favorite favorite = new Favorite();
            favorite.setUser(user.get());
            favorite.setWord(word.get());
            favoriteRepository.save(favorite);
            return ResponseEntity.ok("Thêm vào yêu thích thành công!");
        }
        return ResponseEntity.badRequest().body("Lỗi khi thêm từ yêu thích.");
    }

    // ✅ API XÓA TỪ YÊU THÍCH
    @DeleteMapping("/{userId}/{wordId}")
    @Transactional
    public ResponseEntity<String> removeFavorite(@PathVariable Long userId, @PathVariable Long wordId) {
        Optional<User> user = userRepository.findById(userId);

        if (user.isPresent()) {
            favoriteRepository.deleteByUserAndWordId(user.get(), wordId);
            return ResponseEntity.ok("Đã xóa khỏi yêu thích!");
        }
        return ResponseEntity.badRequest().body("Lỗi khi xóa từ yêu thích.");
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<String> removeAllFavorites(@PathVariable Long userId) {
        Optional<User> user = userRepository.findById(userId);

        if (user.isPresent()) {
            List<Favorite> favorites = favoriteRepository.findByUser(user.get());
            favoriteRepository.deleteAll(favorites);
            return ResponseEntity.ok("Đã xóa tất cả từ yêu thích!");
        }
        return ResponseEntity.badRequest().body("Lỗi khi xóa tất cả từ yêu thích.");
    }
}
