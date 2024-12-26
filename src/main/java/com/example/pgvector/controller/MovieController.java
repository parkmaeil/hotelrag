package com.example.pgvector.controller;

import com.example.pgvector.util.URLTest;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class MovieController {

    private final VectorStore vectorStore;
    private final ChatClient chatClient;

    public MovieController(VectorStore vectorStore, ChatClient.Builder chatClient) {
        this.vectorStore = vectorStore;
        this.chatClient = chatClient.build();
    }

    @GetMapping("/movie")
    public String getRecommendationForm() {
        return "movieRAG";  // returns the HTML file 'recommend.html'
    }

    @PostMapping("/recommend")
    public String recommendMovies1(@RequestParam("query") String query, Model model) throws Exception {
        // Fetch similar movies using vector store
        List<Document> results = vectorStore.similaritySearch(SearchRequest.query(query).withSimilarityThreshold(0.85).withTopK(1));

        if (!results.isEmpty()) {
            Document topResult = results.get(0);
            String movieContent = topResult.getContent();
            String title=movieContent.substring(movieContent.indexOf("(")+1, movieContent.lastIndexOf(")"));
            // Use Jsoup to fetch the YouTube URL
            List<String> url = URLTest.searchYouTube(title);
            model.addAttribute("title", title);
            // Add the movie details and YouTube URL to the model
            model.addAttribute("results", movieContent);
            model.addAttribute("youtubeUrls", url);
        }else{
            model.addAttribute("message", "No closely related movies found.");
        }
        model.addAttribute("query", query);

        return "movieRAG";  // Renders the 'movieRAG.html' view
    }
}
