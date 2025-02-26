package com.rmarcello.demo.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import com.rmarcello.note.beans.Note;
import com.rmarcello.note.service.NoteService;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
                    classes = com.rmarcello.note.SpringBootDemoApplication.class)
class NoteServiceTest {

    private NoteService noteService;

    @BeforeEach
    void setUp() {
        noteService = new NoteService();
    }

    @Test
    void testGetAll() {
        Note note1 = new Note(1, "Title1", "Content1", Arrays.asList("Label1"), Arrays.asList("URL1"), "#FF0000");
        Note note2 = new Note(2, "Title2", "Content2", Arrays.asList("Label2"), Arrays.asList("URL2"), "#00FF00");
        noteService.add(note1);
        noteService.add(note2);

        List<Note> notes = noteService.getAll();
        assertEquals(2, notes.size());
        assertTrue(notes.contains(note1));
        assertTrue(notes.contains(note2));
    }

    @Test
    void testGetById() {
        Note note1 = new Note(1, "Title1", "Content1", Arrays.asList("Label1"), Arrays.asList("URL1"), "#FF0000");
        Note note2 = new Note(2, "Title2", "Content2", Arrays.asList("Label2"), Arrays.asList("URL2"), "#00FF00");
        noteService.add(note1);
        noteService.add(note2);

        Note foundNote = noteService.getById(1);
        assertNotNull(foundNote);
        assertEquals(note1, foundNote);
    }

    @Test
    void testAdd() {
        Note note = new Note(1, "Title1", "Content1", Arrays.asList("Label1"), Arrays.asList("URL1"), "#FF0000");
        noteService.add(note);

        List<Note> notes = noteService.getAll();
        assertEquals(1, notes.size());
        assertTrue(notes.contains(note));
    }

    @Test
    void testRemove() {
        Note note1 = new Note(1, "Title1", "Content1", Arrays.asList("Label1"), Arrays.asList("URL1"), "#FF0000");
        Note note2 = new Note(2, "Title2", "Content2", Arrays.asList("Label2"), Arrays.asList("URL2"), "#00FF00");
        noteService.add(note1);
        noteService.add(note2);

        noteService.remove(1);
        List<Note> notes = noteService.getAll();
        assertEquals(1, notes.size());
        assertFalse(notes.contains(note1));
        assertTrue(notes.contains(note2));
    }

    @Test
    void testGetByLabel() {
        Note note1 = new Note(1, "Title1", "Content1", Arrays.asList("Label1"), Arrays.asList("URL1"), "#FF0000");
        Note note2 = new Note(2, "Title2", "Content2", Arrays.asList("Label2"), Arrays.asList("URL2"), "#00FF00");
        Note note3 = new Note(3, "Title3", "Content3", Arrays.asList("Label1"), Arrays.asList("URL3"), "#0000FF");
        noteService.add(note1);
        noteService.add(note2);
        noteService.add(note3);

        List<Note> notes = noteService.getByLabel("Label1");
        assertEquals(2, notes.size());
        assertTrue(notes.contains(note1));
        assertTrue(notes.contains(note3));
    }

    @Test
    void testUpdate() {
        Note note1 = new Note(1, "Title1", "Content1", Arrays.asList("Label1"), Arrays.asList("URL1"), "#FF0000");
        noteService.add(note1);

        Note updatedNote = new Note(1, "Updated Title", "Updated Content", Arrays.asList("Updated Label"), Arrays.asList("Updated URL"), "#00FF00");
        Note result = noteService.update(1, updatedNote);

        assertNotNull(result);
        assertEquals("Updated Title", result.getTitle());
        assertEquals("Updated Content", result.getContent());
        assertEquals(Arrays.asList("Updated Label"), result.getLabels());
        assertEquals(Arrays.asList("Updated URL"), result.getUrls());
        assertEquals("#00FF00", result.getColor());

        Note notFoundNote = noteService.update(2, updatedNote);
        assertNull(notFoundNote);
    }

}
