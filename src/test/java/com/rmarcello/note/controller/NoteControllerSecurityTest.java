package com.rmarcello.note.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rmarcello.note.beans.Note;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import com.rmarcello.note.service.NoteService;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;

@WebMvcTest(NoteController.class)
public class NoteControllerSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private NoteService noteService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testGetAllNotesWithoutAuthentication_ShouldReturn401() throws Exception {
        mockMvc.perform(get("/notes"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "USER")
    public void testGetAllNotesWithUserRole_ShouldReturn200() throws Exception {
        List<Note> notes = Arrays.asList(
                new Note(1, "Test Note", "Test Content", Arrays.asList("test"), Arrays.asList(), "#FF0000")
        );
        when(noteService.getAll()).thenReturn(notes);

        mockMvc.perform(get("/notes"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "USER")
    public void testDeleteNoteWithUserRole_ShouldReturn403() throws Exception {
        mockMvc.perform(delete("/notes/1"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testDeleteNoteWithAdminRole_ShouldReturn204() throws Exception {
        Note note = new Note(1, "Test Note", "Test Content", Arrays.asList("test"), Arrays.asList(), "#FF0000");
        when(noteService.getById(1)).thenReturn(note);

        mockMvc.perform(delete("/notes/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(roles = "USER")
    public void testCreateNoteWithInvalidData_ShouldReturn400() throws Exception {
        Note invalidNote = new Note(0, "", "", null, null, "invalid-color");

        mockMvc.perform(post("/notes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidNote)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Validation failed"));
    }

    @Test
    @WithMockUser(roles = "USER")
    public void testCreateNoteWithValidData_ShouldReturn201() throws Exception {
        Note validNote = new Note(0, "Valid Title", "Valid Content", Arrays.asList("valid"), Arrays.asList(), "#FF0000");
        Note createdNote = new Note(1, "Valid Title", "Valid Content", Arrays.asList("valid"), Arrays.asList(), "#FF0000");
        
        when(noteService.add(any(Note.class))).thenReturn(createdNote);

        mockMvc.perform(post("/notes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validNote)))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(roles = "USER")
    public void testGetNoteByInvalidId_ShouldReturn400() throws Exception {
        mockMvc.perform(get("/notes/-1"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "USER")
    public void testGetNotesByInvalidLabel_ShouldReturn400() throws Exception {
        mockMvc.perform(get("/notes/label/invalid@label"))
                .andExpect(status().isBadRequest());
    }
}