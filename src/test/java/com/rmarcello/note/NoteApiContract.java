package com.rmarcello.note;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rmarcello.note.beans.Note;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public abstract class NoteApiContract {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    protected void exerciseCrudAndLabelContract() throws Exception {
        Note input = new Note(999, "Contract note", "Content",
                List.of("contract", "other", "contract"),
                List.of("https://example.com/z", "https://example.com/a", "https://example.com/z"),
                "#123456");
        String response = mockMvc.perform(post("/notes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value(input.getTitle()))
                .andExpect(jsonPath("$.content").value(input.getContent()))
                .andExpect(jsonPath("$.labels[0]").value("contract"))
                .andExpect(jsonPath("$.labels[2]").value("contract"))
                .andExpect(jsonPath("$.urls[1]").value("https://example.com/a"))
                .andExpect(jsonPath("$.color").value("#123456"))
                .andReturn().getResponse().getContentAsString();
        int id = objectMapper.readTree(response).get("id").intValue();
        assertThat(id).isPositive().isNotEqualTo(999);

        try {
            mockMvc.perform(get("/notes/{id}", id))
                    .andExpect(status().isOk())
                    .andExpect(content().json(response));
            mockMvc.perform(get("/notes/label/contract"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(1))
                    .andExpect(jsonPath("$[0].id").value(id));
            mockMvc.perform(get("/notes/label/Contract"))
                    .andExpect(status().isOk())
                    .andExpect(content().json("[]"));

            Note replacement = new Note(999, "Updated title", "Updated content",
                    List.of("new-label"), List.of("https://example.com/new"), "#654321");
            mockMvc.perform(put("/notes/{id}", id)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(replacement)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(id))
                    .andExpect(jsonPath("$.title").value("Updated title"))
                    .andExpect(jsonPath("$.content").value("Updated content"))
                    .andExpect(jsonPath("$.labels").value(List.of("new-label")))
                    .andExpect(jsonPath("$.urls").value(List.of("https://example.com/new")))
                    .andExpect(jsonPath("$.color").value("#654321"));
            mockMvc.perform(get("/notes/label/contract"))
                    .andExpect(status().isOk())
                    .andExpect(content().json("[]"));

            mockMvc.perform(put("/notes/{id}", id)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"title\":\"Empty collections\",\"labels\":null,\"urls\":null}"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.labels").isEmpty())
                    .andExpect(jsonPath("$.urls").isEmpty());
            mockMvc.perform(get("/notes/{id}", id))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.title").value("Empty collections"))
                    .andExpect(jsonPath("$.labels").isEmpty())
                    .andExpect(jsonPath("$.urls").isEmpty());
        } finally {
            mockMvc.perform(delete("/notes/{id}", id)).andExpect(status().isOk());
        }

        mockMvc.perform(get("/notes/{id}", id))
                .andExpect(status().isOk())
                .andExpect(content().string(""));
        mockMvc.perform(delete("/notes/{id}", id))
                .andExpect(status().isOk())
                .andExpect(content().string(""));
        mockMvc.perform(put("/notes/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isNotFound());
        mockMvc.perform(get("/notes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(4));
    }
}
