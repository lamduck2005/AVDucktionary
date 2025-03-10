package com.example.avdict.component;

import com.example.avdict.entity.Meaning;
import com.example.avdict.entity.Word;
import com.example.avdict.entity.WordType;
import com.example.avdict.repository.MeaningRepository;
import com.example.avdict.repository.WordRepository;
import com.example.avdict.repository.WordTypeRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

@Component
public class DataLoader {

    @Autowired
    private WordRepository wordRepository;
    @Autowired
    private WordTypeRepository wordTypeRepository;
    @Autowired
    private MeaningRepository meaningRepository;

    @PostConstruct
    public void loadData() throws Exception {
        if (wordRepository.count() > 0) {
            return;
        }

        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream("src/main/resources/data.txt"), StandardCharsets.UTF_8));
        String line;
        Word currentWord = null;
        WordType currentWordType = null;
        List<String> meanings = new ArrayList<>();
        StringBuilder exampleSentences = new StringBuilder();
        StringBuilder idioms = new StringBuilder();

        while ((line = reader.readLine()) != null) {
            line = line.trim();
            if (line.isEmpty()) {
                continue;
            }

            if (line.startsWith("@")) { // Từ mới
                if (currentWord != null) {
                    saveWord(currentWord, exampleSentences.toString(), idioms.toString());
                    exampleSentences.setLength(0);
                    idioms.setLength(0);
                }
                String[] parts = line.split(" ", 2);
                String wordText = parts[0].substring(1);
                String phonetic = parts.length > 1 ? parts[1] : "";
                currentWord = new Word(wordText, phonetic);
                wordRepository.save(currentWord);
            } else if (line.startsWith("*") && currentWord != null) { // Loại từ
                if (currentWordType != null) {
                    saveWordType(currentWordType, meanings);
                    meanings.clear();
                }
                currentWordType = new WordType(line.substring(2).trim());
                currentWordType.setWord(currentWord);
                wordTypeRepository.save(currentWordType);
            } else if (line.startsWith("-") && currentWordType != null) { // Nghĩa
                meanings.add(line.substring(2).trim());
            } else if (line.startsWith("=") && currentWord != null) { // Câu ví dụ
                if (exampleSentences.length() > 0) exampleSentences.append(" | ");
                exampleSentences.append(line.substring(1).trim());
            } else if (line.startsWith("!") && currentWord != null) { // Thành ngữ
                if (idioms.length() > 0) idioms.append(" | ");
                idioms.append(line.substring(1).trim());
            } else {
                System.err.println("Dòng không hợp lệ: " + line);
            }
        }

        if (currentWord != null) {
            saveWord(currentWord, exampleSentences.toString(), idioms.toString());
        }
        reader.close();
    }

    private void saveWord(Word word, String exampleSentences, String idioms) {
        word.setExampleSentences(exampleSentences);
        word.setIdioms(idioms);
        wordRepository.save(word);
    }


    private void saveWordType(WordType wordType, List<String> meanings) {
        wordTypeRepository.save(wordType);
        for (String meaningText : meanings) {
            Meaning meaning = new Meaning(meaningText);
            meaning.setWordType(wordType);
            meaningRepository.save(meaning);
        }
    }

}
