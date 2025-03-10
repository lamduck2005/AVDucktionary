package com.example.avdict.repository;

import com.example.avdict.entity.Word;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WordRepository extends JpaRepository<Word, Long> {
    List<Word> findTop10ByWordStartingWithIgnoreCase(String word);

    Page<Word> findByWordContainingIgnoreCase(String word, Pageable pageable);
}