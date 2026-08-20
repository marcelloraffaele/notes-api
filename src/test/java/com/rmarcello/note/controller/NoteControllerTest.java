package com.rmarcello.note.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.rmarcello.note.SpringBootDemoApplication;

@SpringBootTest(classes = SpringBootDemoApplication.class)
@AutoConfigureMockMvc
class NoteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void createsUpdatesAndRemovesOptionalMetadata() throws Exception {
        String note = """
                {"title":"Idea","content":"Content","labels":[],"urls":[],"emoji":"⭐",
                "imageUrl":"https://example.com/image.jpg","priority":3}""";

        String response = mockMvc.perform(post("/notes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(note))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.emoji").value("⭐"))
                .andExpect(jsonPath("$.imageUrl").value("https://example.com/image.jpg"))
                .andExpect(jsonPath("$.priority").value(3))
                .andReturn().getResponse().getContentAsString();

        long id = ((Number) com.jayway.jsonpath.JsonPath.read(response, "$.id")).longValue();
        String updatedNote = """
                {"title":"Idea","content":"Content","labels":[],"urls":[],"emoji":null,"imageUrl":null,"priority":0}""";

        mockMvc.perform(put("/notes/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatedNote))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.emoji").doesNotExist())
                .andExpect(jsonPath("$.imageUrl").doesNotExist())
                .andExpect(jsonPath("$.priority").value(0));
    }

    @Test
    void rejectsInvalidOptionalMetadata() throws Exception {
        String invalidNote = """
                {"title":"Idea","content":"Content","labels":[],"urls":[],"emoji":"not-an-emoji",
                "imageUrl":"not-a-url","priority":6}""";

        mockMvc.perform(post("/notes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidNote))
                .andExpect(status().isBadRequest());
    }
}
