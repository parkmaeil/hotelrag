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

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Controller
public class MovieController {

    private final VectorStore vectorStore;
    // 추가
    private final ChatClient chatClient;

    public MovieController(VectorStore vectorStore, ChatClient.Builder chatClient) {
        this.vectorStore = vectorStore;
        this.chatClient = chatClient.build();
    }

    @GetMapping("/movie")
    public String getRecommendationForm() {
        return "movieRAG";  // returns the HTML file 'recommend.html'
    }

/*    @PostMapping("/recommend")
    public String recommendMovies(@RequestParam("query") String query, Model model) {
        List<Document> results =
                vectorStore.similaritySearch(SearchRequest.query(query).withTopK(1));
        model.addAttribute("results", results);
        model.addAttribute("query", query);
        return "movieRAG";
    }*/

    // 추가
    @PostMapping("/recommend")
    public String recommendMovies1(@RequestParam("query") String query, Model model) throws Exception {
        // Fetch similar movies using vector store
        List<Document> results = vectorStore.similaritySearch(SearchRequest.query(query).withSimilarityThreshold(0.85).withTopK(1));

        if (!results.isEmpty()) {
            Document topResult = results.get(0);
            String movieContent = topResult.getContent();
            Pattern pattern = Pattern.compile("\\(([^)]+)\\)");
            Matcher matcher = pattern.matcher(movieContent);
            matcher.find();
            // Use Jsoup to fetch the YouTube URL
            List<String> url = URLTest.searchYouTube(matcher.group(1));
            model.addAttribute("title", matcher.group(1));
            // Add the movie details and YouTube URL to the model
            model.addAttribute("results", topResult);
            model.addAttribute("youtubeUrls", url);
        }else{
            model.addAttribute("message", "No closely related movies found.");
        }
        model.addAttribute("query", query);


        return "movieRAG";  // Renders the 'movieRAG.html' view
    }
}
