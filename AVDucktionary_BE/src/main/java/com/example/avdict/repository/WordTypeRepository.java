package com.example.avdict.repository;

import com.example.avdict.entity.WordType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WordTypeRepository extends JpaRepository<WordType, Long> {
}