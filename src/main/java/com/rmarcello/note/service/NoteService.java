package com.rmarcello.note.service;

import org.springframework.stereotype.Service;

import com.rmarcello.note.beans.Note;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class NoteService {

    private List<Note> notes = new ArrayList<>();

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
