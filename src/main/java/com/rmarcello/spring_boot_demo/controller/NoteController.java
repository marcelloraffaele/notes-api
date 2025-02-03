package com.rmarcello.spring_boot_demo.controller;

import com.rmarcello.spring_boot_demo.beans.Note;
import com.rmarcello.spring_boot_demo.service.NoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public void addNote(@RequestBody Note note) {
        noteService.add(note);
    }

    @DeleteMapping("/{id}")
    public void removeNote(@PathVariable int id) {
        noteService.remove(id);
    }

    @GetMapping("/label/{label}")
    public List<Note> getNotesByLabel(@PathVariable String label) {
        return noteService.getByLabel(label);
    }
}
