package com.rmarcello.note.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
import com.rmarcello.note.service.RateLimitingService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;

import java.util.List;

@RestController
@RequestMapping("/notes")
@Validated
public class NoteController {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(NoteController.class);

    @Autowired
    private NoteService noteService;
    
    @Autowired
    private RateLimitingService rateLimitingService;

    @GetMapping
    public ResponseEntity<List<Note>> getAllNotes(HttpServletRequest request) {
        if (!rateLimitingService.allowRequest(getClientIpAddress(request))) {
            LOGGER.warn("Rate limit exceeded for IP: {} on GET /notes", getClientIpAddress(request));
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).build();
        }
        
        String username = getCurrentUsername();
        LOGGER.info("User {} requested all notes", username);
        
        List<Note> notes = noteService.getAll();
        return ResponseEntity.ok(notes);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Note> getNoteById(@PathVariable @Min(1) int id, HttpServletRequest request) {
        if (!rateLimitingService.allowRequest(getClientIpAddress(request))) {
            LOGGER.warn("Rate limit exceeded for IP: {} on GET /notes/{}", getClientIpAddress(request), id);
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).build();
        }
        
        String username = getCurrentUsername();
        Note note = noteService.getById(id);
        
        if (note == null) {
            LOGGER.info("User {} requested non-existent note with id: {}", username, id);
            return ResponseEntity.notFound().build();
        }
        
        LOGGER.info("User {} accessed note id: {}", username, id);
        return ResponseEntity.ok(note);
    }

    @PostMapping
    public ResponseEntity<Note> addNote(@Valid @RequestBody Note note, HttpServletRequest request) {
        if (!rateLimitingService.allowRequest(getClientIpAddress(request))) {
            LOGGER.warn("Rate limit exceeded for IP: {} on POST /notes", getClientIpAddress(request));
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).build();
        }
        
        String username = getCurrentUsername();
        Note savedNote = noteService.add(note);
        
        LOGGER.info("User {} created new note with id: {}", username, savedNote.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(savedNote);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> removeNote(@PathVariable @Min(1) int id, HttpServletRequest request) {
        if (!rateLimitingService.allowRequest(getClientIpAddress(request))) {
            LOGGER.warn("Rate limit exceeded for IP: {} on DELETE /notes/{}", getClientIpAddress(request), id);
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).build();
        }
        
        String username = getCurrentUsername();
        Note existingNote = noteService.getById(id);
        
        if (existingNote == null) {
            LOGGER.info("User {} attempted to delete non-existent note with id: {}", username, id);
            return ResponseEntity.notFound().build();
        }
        
        noteService.remove(id);
        LOGGER.info("User {} deleted note with id: {}", username, id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/label/{label}")
    public ResponseEntity<List<Note>> getNotesByLabel(@PathVariable String label, HttpServletRequest request) {
        if (!rateLimitingService.allowRequest(getClientIpAddress(request))) {
            LOGGER.warn("Rate limit exceeded for IP: {} on GET /notes/label/{}", getClientIpAddress(request), label);
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).build();
        }
        
        // Input sanitization for label
        String sanitizedLabel = sanitizeInput(label);
        if (sanitizedLabel == null || sanitizedLabel.trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        
        String username = getCurrentUsername();
        List<Note> notes = noteService.getByLabel(sanitizedLabel);
        
        LOGGER.info("User {} searched notes by label: {}", username, sanitizedLabel);
        return ResponseEntity.ok(notes);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Note> updateNote(@PathVariable @Min(1) int id, @Valid @RequestBody Note note, 
                                         HttpServletRequest request) {
        if (!rateLimitingService.allowRequest(getClientIpAddress(request))) {
            LOGGER.warn("Rate limit exceeded for IP: {} on PUT /notes/{}", getClientIpAddress(request), id);
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).build();
        }
        
        String username = getCurrentUsername();
        Note updatedNote = noteService.update(id, note);
        
        if (updatedNote == null) {
            LOGGER.info("User {} attempted to update non-existent note with id: {}", username, id);
            return ResponseEntity.notFound().build();
        }
        
        LOGGER.info("User {} updated note with id: {}", username, id);
        return ResponseEntity.ok(updatedNote);
    }
    
    private String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null ? authentication.getName() : "anonymous";
    }
    
    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedForHeader = request.getHeader("X-Forwarded-For");
        if (xForwardedForHeader == null) {
            return request.getRemoteAddr();
        } else {
            return xForwardedForHeader.split(",")[0];
        }
    }
    
    private String sanitizeInput(String input) {
        if (input == null) {
            return null;
        }
        
        // Remove potentially dangerous characters and limit length
        return input.replaceAll("[<>\"'&]", "").trim().substring(0, Math.min(input.length(), 50));
    }
}
