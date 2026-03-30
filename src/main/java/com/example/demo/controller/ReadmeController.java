package com.example.demo.controller;

import com.example.demo.service.OpenAIService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class ReadmeController {

    @Autowired
    private OpenAIService openAIService;

    @PostMapping(
            value = "/generate-readme",
            consumes = MediaType.TEXT_PLAIN_VALUE,
            produces = MediaType.TEXT_PLAIN_VALUE
    )
    public String generateReadme(
            @RequestBody String code,
            @RequestParam(name = "verbosity", defaultValue = "normal") String verbosity
    ) {
        return openAIService.generateReadme(code, verbosity);
    }
}