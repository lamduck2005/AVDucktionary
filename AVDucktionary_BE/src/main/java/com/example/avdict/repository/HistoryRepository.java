package com.example.avdict.repository;

import com.example.avdict.entity.History;
import com.example.avdict.entity.User;
import com.example.avdict.entity.Word;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface HistoryRepository extends JpaRepository<History, Long> {
    Optional<History> findByUserAndWord(User user, Word word);

    @Query("SELECT h FROM History h WHERE h.user.id = :userId ORDER BY h.viewedAt DESC LIMIT :limit")
    List<History> findRecentHistory(@Param("userId") Long userId, @Param("limit") int limit);

    List<History> findByUser(User user);

    void deleteByUserAndWord(User user, Word word);
}