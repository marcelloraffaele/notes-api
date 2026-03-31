package com.rmarcello.note.service;

import org.springframework.stereotype.Service;

import com.rmarcello.note.beans.Note;
import com.rmarcello.note.exception.NoteAlreadyPresentException;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class NoteService {

    private Map<Long, Note> notes = new LinkedHashMap<>();

    public List<Note> getAll() {
        return new ArrayList<>(notes.values());
    }

    public Note getById(int id) {
        return notes.get((long) id);
    }

    public Note add(Note note) {
        if (note.getId() != 0 && notes.containsKey(note.getId())) {
            throw new NoteAlreadyPresentException("Note with id " + note.getId() + " already present");
        }
        if (note.getId() == 0) {
            note.setId(getNextId());
        }
        notes.put(note.getId(), note);
        return note;
    }

    private long getNextId() {
        return notes.keySet().stream()
                .mapToLong(Long::longValue)
                .max()
                .orElse(0) + 1;
    }

    public void remove(int id) {
        notes.remove((long) id);
    }

    public List<Note> getByLabel(String label) {
        return notes.values().stream()
                .filter(note -> note.getLabels().contains(label))
                .collect(Collectors.toList());
    }

    public Note update(int id, Note updatedNote) {
        Note existingNote = getById(id);
        if (existingNote == null) {
            return null;
        }
        existingNote.setTitle(updatedNote.getTitle());
        existingNote.setContent(updatedNote.getContent());
        existingNote.setLabels(updatedNote.getLabels());
        existingNote.setUrls(updatedNote.getUrls());
        existingNote.setColor(updatedNote.getColor());
        return existingNote;
    }
}
