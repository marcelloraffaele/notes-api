package com.rmarcello.note.service;

import java.util.List;

import com.rmarcello.note.beans.Note;

public interface NoteOperations {
    List<Note> getAll();

    Note getById(int id);

    Note add(Note note);

    void remove(int id);

    List<Note> getByLabel(String label);

    Note update(int id, Note updatedNote);
}
