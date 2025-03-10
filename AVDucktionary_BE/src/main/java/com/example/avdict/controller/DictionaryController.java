package com.example.avdict.controller;

import com.example.avdict.entity.Word;
import com.example.avdict.repository.WordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/api/dictionary")
@ResponseBody
public class DictionaryController {

//    @Autowired
//    private WordRepository wordRepository;
//
//
//
}
