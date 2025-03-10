package com.example.avdict.repository;

import com.example.avdict.entity.Note;
import com.example.avdict.entity.User;
import com.example.avdict.entity.Word;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface NoteRepository extends JpaRepository<Note, Long> {
    List<Note> findByUserAndWord(User user, Word word);
    void deleteByUserAndWord(User user, Word word);

    @Query("SELECT DISTINCT n.word FROM Note n WHERE n.user.id = :userId")
    List<Word> findDistinctWordsByUserId(@Param("userId") Long userId);

    List<Note> findByUser(User user);
}
