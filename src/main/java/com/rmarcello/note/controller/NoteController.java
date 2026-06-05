package com.rmarcello.note.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rmarcello.note.beans.Note;
import com.rmarcello.note.service.NoteService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.Principal;
import java.util.List;

@Validated
@RestController
@RequestMapping("/notes")
public class NoteController {

    private static final Logger SECURITY_LOGGER = LoggerFactory.getLogger("SECURITY");
    private static final Logger logger = LoggerFactory.getLogger(NoteController.class);

    @Autowired
    private NoteService noteService;

    @GetMapping
    public ResponseEntity<List<Note>> getAllNotes(Principal principal) {
        SECURITY_LOGGER.info("User {} accessed all notes", principal.getName());
        List<Note> notes = noteService.getAll();
        return ResponseEntity.ok(notes);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Note> getNoteById(
            @PathVariable @Min(value = 1, message = "ID must be a positive number") int id,
            Principal principal) {
        SECURITY_LOGGER.info("User {} accessed note with ID: {}", principal.getName(), id);
        Note note = noteService.getById(id);
        if (note == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(note);
    }

    @PostMapping
    public ResponseEntity<Note> addNote(@Valid @RequestBody Note note, Principal principal) {
        SECURITY_LOGGER.info("User {} created a new note with title: {}", principal.getName(), note.getTitle());
        Note createdNote = noteService.add(note);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdNote);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> removeNote(
            @PathVariable @Min(value = 1, message = "ID must be a positive number") int id,
            Principal principal) {
        SECURITY_LOGGER.info("User {} deleted note with ID: {}", principal.getName(), id);
        Note existingNote = noteService.getById(id);
        if (existingNote == null) {
            return ResponseEntity.notFound().build();
        }
        noteService.remove(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/label/{label}")
    public ResponseEntity<List<Note>> getNotesByLabel(
            @PathVariable @Pattern(regexp = "^[a-zA-Z0-9_-]{1,50}$", message = "Label must contain only alphanumeric characters, hyphens, and underscores, max 50 characters") String label,
            Principal principal) {
        SECURITY_LOGGER.info("User {} searched notes by label: {}", principal.getName(), label);
        List<Note> notes = noteService.getByLabel(label);
        return ResponseEntity.ok(notes);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Note> updateNote(
            @PathVariable @Min(value = 1, message = "ID must be a positive number") int id,
            @Valid @RequestBody Note note,
            Principal principal) {
        SECURITY_LOGGER.info("User {} updated note with ID: {}", principal.getName(), id);
        Note updatedNote = noteService.update(id, note);
        if (updatedNote == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updatedNote);
    }
}
