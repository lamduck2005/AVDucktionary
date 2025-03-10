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
@Table(name = "word_types")
public class WordType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "word_id")
    @JsonIgnore
    private Word word;

    @Column(name = "type", columnDefinition = "NVARCHAR(255)")
    private String type;

    @OneToMany(mappedBy = "wordType", cascade = CascadeType.ALL)
    private List<Meaning> meanings;

    public WordType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "WordType{id=" + id + ", type='" + type + "'}";
    }

}
