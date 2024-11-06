package com.example.pgvector.controller;

import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
public class ChatController {

    private final VectorStore vectorStore;

    public ChatController(VectorStore vectorStore){
        this.vectorStore=vectorStore;
    }

    @GetMapping("/chat")
    public String chat(String message) {
        List<Document> results =
                vectorStore.similaritySearch(SearchRequest.query(message).withTopK(2));
        return results.stream().map(Document::getContent)
                .limit(10)
                .collect(Collectors.joining(", "));
        }

    }


