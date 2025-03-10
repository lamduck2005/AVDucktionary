package com.example.avdict.repository;

import com.example.avdict.entity.Favorite;
import com.example.avdict.entity.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
    List<Favorite> findByUser(User user);

    @Transactional
    @Modifying
    @Query("DELETE FROM Favorite f WHERE f.user = :user AND f.word.id = :wordId")
    void deleteByUserAndWordId(User user, Long wordId);
}