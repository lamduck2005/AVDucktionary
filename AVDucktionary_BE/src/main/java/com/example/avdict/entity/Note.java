package com.example.avdict.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "notes")
public class Note {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // Người dùng

    @ManyToOne
    @JoinColumn(name = "word_id", nullable = false)
    private Word word; // Từ vựng

    @Column(nullable = false, columnDefinition = "NVARCHAR(1000)")

    private String content;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now(); // Thời gian tạo ghi chú
}
