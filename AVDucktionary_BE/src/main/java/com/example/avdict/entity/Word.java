package com.example.avdict.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "words")
public class Word {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "word", columnDefinition = "NVARCHAR(255)")
    private String word;

    @Column(name = "phonetic", columnDefinition = "NVARCHAR(255)")
    private String phonetic;

    @OneToMany(mappedBy = "word", cascade = CascadeType.ALL)
    private List<WordType> wordTypes;

    @Column(name = "example_sentences", columnDefinition = "NVARCHAR(MAX)")
    private String exampleSentences;

    @Column(name = "idioms", columnDefinition = "NVARCHAR(MAX)")
    private String idioms;

    public Word(String word, String phonetic) {
        this.word = word;
        this.phonetic = phonetic;
        this.exampleSentences = "";
        this.idioms = "";
    }

    @Override
    public String toString() {
        return "Word{id=" + id + ", word='" + word + "', phonetic='" + phonetic + "'}";
    }

}
