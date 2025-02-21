package com.rmarcello.note.service;

import org.springframework.stereotype.Service;

import com.rmarcello.note.beans.Note;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class NoteService {

    private List<Note> notes = new ArrayList<>();

    public NoteService() {
        // Sample data
        notes.add(new Note(1, "Meeting Notes", "Discuss project milestones and deadlines.", List.of("meeting", "project"), List.of("https://example.com/meeting1", "https://example.com/meeting2")));
        notes.add(new Note(2, "Shopping List", "Buy groceries for the week.", List.of("personal", "shopping"), List.of("https://example.com/groceries")));
        notes.add(new Note(3, "Workout Plan", "Weekly workout schedule.", List.of("fitness", "health"), List.of("https://example.com/workout")));
        notes.add(new Note(4, "Book Recommendations", "List of books to read.", List.of("reading", "books"), List.of("https://example.com/books1", "https://example.com/books2")));
    }

    public List<Note> getAll() {
        return notes;
    }

    public Note getById(int id) {
        return notes.stream()
                .filter(note -> note.getId() == id)
                .findFirst()
                .orElse(null);
    }

    public void add(Note note) {
        notes.add(note);
    }

    public void remove(int id) {
        notes.removeIf(note -> note.getId() == id);
    }

    public List<Note> getByLabel(String label) {
        return notes.stream()
                .filter(note -> note.getLabels().contains(label))
                .collect(Collectors.toList());
    }
}
