package com.example.avdict.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "meanings")
public class Meaning {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "word_type_id")
    @JsonIgnore
    private WordType wordType;

    @Column(name = "meaning", columnDefinition = "NVARCHAR(MAX)")
    private String meaning;

    public Meaning(String meaning) {
        this.meaning = meaning;
    }
}
