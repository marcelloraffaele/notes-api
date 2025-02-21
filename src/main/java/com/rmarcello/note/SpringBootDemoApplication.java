package com.rmarcello.note;

import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.rmarcello.note.beans.Note;
import com.rmarcello.note.service.NoteService;

@SpringBootApplication
public class SpringBootDemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringBootDemoApplication.class, args);
	}

	@Bean
    CommandLineRunner init(NoteService noteService) {
        return args -> {
            // Sample data
			noteService.add(new Note(1, "Meeting Notes", "Discuss project milestones and deadlines.", List.of("meeting", "project"), List.of("https://example.com/meeting1", "https://example.com/meeting2")));
			noteService.add(new Note(2, "Shopping List", "Buy groceries for the week.", List.of("personal", "shopping"), List.of("https://example.com/groceries")));
			noteService.add(new Note(3, "Workout Plan", "Weekly workout schedule.", List.of("fitness", "health"), List.of("https://example.com/workout")));
			noteService.add(new Note(4, "Book Recommendations", "List of books to read.", List.of("reading", "books"), List.of("https://example.com/books1", "https://example.com/books2")));
            System.out.println("Demo data initialized successfully!");
        };
    }

}
