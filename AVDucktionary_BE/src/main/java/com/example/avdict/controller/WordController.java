package com.example.avdict.controller;

import com.example.avdict.entity.Word;
import com.example.avdict.repository.WordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/words")
public class WordController {
    @Autowired
    private WordRepository wordRepository;

    @GetMapping("/words")
    public Map<String, Object> getWords(
            @RequestParam(required = false, defaultValue = "") String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "30") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<Word> wordPage;

        if (keyword.isEmpty()) {
            wordPage = wordRepository.findAll(pageable); // Nếu không có keyword, lấy toàn bộ từ
        } else {
            wordPage = wordRepository.findByWordContainingIgnoreCase(keyword, pageable);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("words", wordPage.getContent());
        response.put("totalPages", wordPage.getTotalPages());
        response.put("currentPage", page);
        response.put("totalElements", wordPage.getTotalElements());
        return response;
    }



    @GetMapping("/suggest")
    public List<Word> suggestWords(@RequestParam String keyword) {
        return wordRepository.findTop10ByWordStartingWithIgnoreCase(keyword);
    }


    @PostMapping("/words/{id}")
    public Word detail(@PathVariable Long id) {
        Word word = wordRepository.findById(id).orElse(new Word());
        return word;
    }

    @GetMapping("/{wordId}")
    public ResponseEntity<?> getWordDetails(@PathVariable Long wordId) {
        Optional<Word> word = wordRepository.findById(wordId);
        if(word.isEmpty()){
            return ResponseEntity.badRequest().body("Word không tồn tại!");
        }
        return word.map(ResponseEntity::ok).get();
    }


}
