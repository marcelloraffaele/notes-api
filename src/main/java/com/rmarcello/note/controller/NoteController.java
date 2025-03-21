package com.rmarcello.note.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
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

import java.util.List;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping("/notes")
public class NoteController {

    @Autowired
    private NoteService noteService;

    @GetMapping
    public List<Note> getAllNotes() {
        return noteService.getAll();
    }

    @GetMapping("/{id}")
    public Note getNoteById(@PathVariable int id) {
        return noteService.getById(id);
    }

    @PostMapping
    public Note addNote(@RequestBody Note note) {
        return noteService.add(note);
    }

    @DeleteMapping("/{id}")
    public void removeNote(@PathVariable int id) {
        noteService.remove(id);
    }

    @GetMapping("/label/{label}")
    public List<Note> getNotesByLabel(@PathVariable String label) {
        return noteService.getByLabel(label);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Note> updateNote(@PathVariable int id, @RequestBody Note note) {
        Note updatedNote = noteService.update(id, note);
        if (updatedNote == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(updatedNote, HttpStatus.OK);
    }

}
