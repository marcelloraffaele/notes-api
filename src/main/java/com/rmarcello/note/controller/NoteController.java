package com.rmarcello.note.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rmarcello.note.beans.Note;
import com.rmarcello.note.service.NoteService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/notes")
@Validated
public class NoteController {

    private static final Logger LOGGER = LoggerFactory.getLogger(NoteController.class);

    @Autowired
    private NoteService noteService;

    @GetMapping
    @PreAuthorize("hasRole('USER')")
    public List<Note> getAllNotes(Principal principal) {
        LOGGER.info("User '{}' requested all notes", principal.getName());
        return noteService.getAll();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Note> getNoteById(
            @PathVariable @Min(value = 1, message = "ID must be positive") @Max(value = Long.MAX_VALUE, message = "ID too large") int id,
            Principal principal) {
        LOGGER.info("User '{}' requested note with ID: {}", principal.getName(), id);
        Note note = noteService.getById(id);
        if (note == null) {
            LOGGER.warn("User '{}' attempted to access non-existent note with ID: {}", principal.getName(), id);
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(note);
    }

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Note> addNote(@Valid @RequestBody Note note, Principal principal) {
        LOGGER.info("User '{}' creating new note with title: '{}'", principal.getName(), note.getTitle());
        try {
            Note createdNote = noteService.add(note);
            LOGGER.info("User '{}' successfully created note with ID: {}", principal.getName(), createdNote.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(createdNote);
        } catch (Exception e) {
            LOGGER.error("Error creating note for user '{}': {}", principal.getName(), e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> removeNote(
            @PathVariable @Min(value = 1, message = "ID must be positive") @Max(value = Long.MAX_VALUE, message = "ID too large") int id,
            Principal principal) {
        LOGGER.info("Admin user '{}' attempting to delete note with ID: {}", principal.getName(), id);
        Note existingNote = noteService.getById(id);
        if (existingNote == null) {
            LOGGER.warn("Admin user '{}' attempted to delete non-existent note with ID: {}", principal.getName(), id);
            return ResponseEntity.notFound().build();
        }
        noteService.remove(id);
        LOGGER.info("Admin user '{}' successfully deleted note with ID: {}", principal.getName(), id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/label/{label}")
    @PreAuthorize("hasRole('USER')")
    public List<Note> getNotesByLabel(
            @PathVariable @Size(min = 1, max = 50, message = "Label must be between 1 and 50 characters")
            @Pattern(regexp = "^[a-zA-Z0-9_-]+$", message = "Label can only contain alphanumeric characters, hyphens, and underscores") String label,
            Principal principal) {
        LOGGER.info("User '{}' requested notes with label: '{}'", principal.getName(), label);
        return noteService.getByLabel(label);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Note> updateNote(
            @PathVariable @Min(value = 1, message = "ID must be positive") @Max(value = Long.MAX_VALUE, message = "ID too large") int id,
            @Valid @RequestBody Note note,
            Principal principal) {
        LOGGER.info("User '{}' attempting to update note with ID: {}", principal.getName(), id);
        try {
            Note updatedNote = noteService.update(id, note);
            if (updatedNote == null) {
                LOGGER.warn("User '{}' attempted to update non-existent note with ID: {}", principal.getName(), id);
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            LOGGER.info("User '{}' successfully updated note with ID: {}", principal.getName(), id);
            return new ResponseEntity<>(updatedNote, HttpStatus.OK);
        } catch (Exception e) {
            LOGGER.error("Error updating note ID {} for user '{}': {}", id, principal.getName(), e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}
