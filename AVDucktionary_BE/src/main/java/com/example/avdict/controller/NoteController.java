package com.example.avdict.controller;

import com.example.avdict.entity.Note;
import com.example.avdict.entity.User;
import com.example.avdict.entity.Word;
import com.example.avdict.repository.NoteRepository;
import com.example.avdict.repository.UserRepository;
import com.example.avdict.repository.WordRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/notes")
public class NoteController {

    @Autowired
    private NoteRepository noteRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private WordRepository wordRepository;

    @GetMapping("/{userId}")
    public ResponseEntity<List<Word>> getWordsWithNotes(@PathVariable Long userId) {
        List<Word> words = noteRepository.findDistinctWordsByUserId(userId);
        return ResponseEntity.ok(words);
    }

    @DeleteMapping("/user/{userId}")
    public ResponseEntity<String> removeAllNotes(@PathVariable Long userId) {
        Optional<User> user = userRepository.findById(userId);

        if (user.isPresent()) {
                List<Note> notes = noteRepository.findByUser(user.get());
                noteRepository.deleteAll(notes);
                return ResponseEntity.ok("Đã xóa tất cả ghi chú!");
        }
        return ResponseEntity.badRequest().body("Lỗi khi xóa tất cả từ yêu thích.");
    }

    // ✅ Lấy danh sách ghi chú của user cho một từ
    @GetMapping("/{userId}/{wordId}")
    public ResponseEntity<List<Note>> getNotes(@PathVariable Long userId, @PathVariable Long wordId) {
        Optional<User> user = userRepository.findById(userId);
        Optional<Word> word = wordRepository.findById(wordId);

        if (user.isPresent() && word.isPresent()) {
            return ResponseEntity.ok(noteRepository.findByUserAndWord(user.get(), word.get()));
        }
        return ResponseEntity.badRequest().build();
    }



    // ✅ Thêm ghi chú
    @PostMapping("/{userId}/{wordId}")
    public ResponseEntity<String> addNote(@PathVariable Long userId, @PathVariable Long wordId, @RequestBody String content) {
        Optional<User> user = userRepository.findById(userId);
        Optional<Word> word = wordRepository.findById(wordId);

        if (user.isPresent() && word.isPresent()) {
            Note note = new Note();
            note.setUser(user.get());
            note.setWord(word.get());
            note.setContent(content);
            noteRepository.save(note);
            return ResponseEntity.ok("Ghi chú đã được thêm!");
        }
        return ResponseEntity.badRequest().body("Lỗi khi thêm ghi chú.");
    }

    // ✅ Xóa ghi chú

    @DeleteMapping("/{noteId}")
    public ResponseEntity<String> removeNote(@PathVariable Long noteId) {
        if (noteRepository.existsById(noteId)) {
            noteRepository.deleteById(noteId);
            return ResponseEntity.ok("Ghi chú đã được xóa!");
        }
        return ResponseEntity.badRequest().body("Ghi chú không tồn tại.");
    }


    @DeleteMapping("/{userId}/{wordId}")
    @Transactional
    public ResponseEntity<String> removeNotes(@PathVariable Long userId, @PathVariable Long wordId) {
        Optional<User> user = userRepository.findById(userId);
        Optional<Word> word = wordRepository.findById(wordId);

        if (user.isPresent() && word.isPresent()) {
            noteRepository.deleteByUserAndWord(user.get(), word.get());
            return ResponseEntity.ok("Đã xóa ghi chú!");
        }
        return ResponseEntity.badRequest().body("Lỗi khi xóa ghi chú.");
    }
}
