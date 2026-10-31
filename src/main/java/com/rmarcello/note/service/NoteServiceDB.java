package com.rmarcello.note.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import com.rmarcello.note.beans.Note;
import com.rmarcello.note.model.NoteEntity;
import com.rmarcello.note.repository.NoteRepository;

@Transactional(readOnly = true)
public class NoteServiceDB implements NoteOperations {
    private final NoteRepository repository;

    public NoteServiceDB(NoteRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<Note> getAll() {
        return repository.findAllByOrderByIdAsc().stream().map(this::toNote).toList();
    }

    @Override
    public Note getById(int id) {
        return repository.findById((long) id).map(this::toNote).orElse(null);
    }

    @Override
    @Transactional
    public Note add(Note note) {
        NoteEntity entity = new NoteEntity();
        copyFields(note, entity);
        NoteEntity saved = repository.save(entity);
        note.setId(saved.getId());
        return toNote(saved);
    }

    @Override
    @Transactional
    public void remove(int id) {
        repository.findById((long) id).ifPresent(repository::delete);
    }

    @Override
    public List<Note> getByLabel(String label) {
        return repository.findByLabel(label).stream().map(this::toNote).toList();
    }

    @Override
    @Transactional
    public Note update(int id, Note updatedNote) {
        return repository.findById((long) id).map(entity -> {
            copyFields(updatedNote, entity);
            return toNote(repository.save(entity));
        }).orElse(null);
    }

    private void copyFields(Note note, NoteEntity entity) {
        entity.setTitle(note.getTitle());
        entity.setContent(note.getContent());
        entity.setLabels(note.getLabels());
        entity.setUrls(note.getUrls());
        entity.setColor(note.getColor());
    }

    private Note toNote(NoteEntity entity) {
        return new Note(entity.getId(), entity.getTitle(), entity.getContent(),
                new ArrayList<>(entity.getLabels()), new ArrayList<>(entity.getUrls()), entity.getColor());
    }
}
