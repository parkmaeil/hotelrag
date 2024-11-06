package com.example.pgvector;

import org.springframework.ai.document.Document;
import org.springframework.ai.transformer.splitter.TextSplitter;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.PgVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.io.Resource;

import java.nio.file.Files;
import java.util.List;
import java.util.stream.Collectors;

@SpringBootApplication
public class PgvectorApplication { // implements  CommandLineRunner

	@Value("classpath:movie_plots_korean.txt")
	Resource resource;

	private final VectorStore vectorStore;
	public PgvectorApplication(VectorStore vectorStore){
		  this.vectorStore=vectorStore;
	}

	public static void main(String[] args) {
		SpringApplication.run(PgvectorApplication.class, args);
	}

   //@Override
	public void run(String... args) throws Exception {

	 	List<Document> documents = Files.lines(resource.getFile().toPath())
				.map(Document::new)
				.collect(Collectors.toList());
		TextSplitter textSplitter = new TokenTextSplitter();
		for(Document document : documents) {
			List<Document> splitteddocs = textSplitter.split(document);
			System.out.println("before adding document: " + document.getContent());
			vectorStore.add(splitteddocs);
			System.out.println("Added document: " + document.getContent());
			Thread.sleep(1000);
		}
	}
}
