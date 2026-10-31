package com.rmarcello.note.controller;

import java.util.List;

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
import com.rmarcello.note.service.NoteOperations;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping("/notes")
@Tag(name = "Notes", description = "Operations for creating and managing notes")
public class NoteController {

    private final NoteOperations noteService;

    public NoteController(NoteOperations noteService) {
        this.noteService = noteService;
    }

    @Operation(
        summary = "List notes",
        description = "Returns all notes, or an empty array when no notes exist."
    )
    @ApiResponse(
        responseCode = "200",
        description = "Notes retrieved successfully",
        content = @Content(
            mediaType = "application/json",
            array = @ArraySchema(schema = @Schema(implementation = Note.class))
        )
    )
    @GetMapping
    public List<Note> getAllNotes() {
        return noteService.getAll();
    }

    @Operation(
        summary = "Get a note",
        description = "Returns a note by ID. If no note exists, returns HTTP 200 with no body."
    )
    @ApiResponse(
        responseCode = "200",
        description = "Note retrieved, or an empty body if the note does not exist",
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = Note.class)
        )
    )
    @GetMapping("/{id}")
    public Note getNoteById(
            @Parameter(description = "ID of the note", required = true, example = "1")
            @PathVariable int id) {
        return noteService.getById(id);
    }

    @Operation(
        summary = "Create a note",
        description = "Creates a note. The server assigns an ID, ignoring any supplied ID."
    )
    @ApiResponse(
        responseCode = "200",
        description = "Note created successfully",
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = Note.class)
        )
    )
    @PostMapping
    public Note addNote(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "Note data to create; the ID is assigned by the server",
                required = true,
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = Note.class)
                )
            )
            @RequestBody Note note) {
        return noteService.add(note);
    }

    @Operation(
        summary = "Delete a note",
        description = "Deletes a note by ID. Deleting a nonexistent note also succeeds."
    )
    @ApiResponse(
        responseCode = "200",
        description = "Deletion completed; no response body",
        content = @Content
    )
    @DeleteMapping("/{id}")
    public void removeNote(
            @Parameter(description = "ID of the note to delete", required = true, example = "1")
            @PathVariable int id) {
        noteService.remove(id);
    }

    @Operation(
        summary = "Find notes by label",
        description = "Returns notes containing the exact, case-sensitive label, "
            + "or an empty array when no notes match."
    )
    @ApiResponse(
        responseCode = "200",
        description = "Matching notes retrieved successfully",
        content = @Content(
            mediaType = "application/json",
            array = @ArraySchema(schema = @Schema(implementation = Note.class))
        )
    )
    @GetMapping("/label/{label}")
    public List<Note> getNotesByLabel(
            @Parameter(description = "Label to match", required = true, example = "work")
            @PathVariable String label) {
        return noteService.getByLabel(label);
    }

    @Operation(
        summary = "Update a note",
        description = "Replaces the title, content, labels, URLs, and color of an existing note. "
            + "The path ID identifies the note; the request-body ID is ignored."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Note updated successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = Note.class)
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "No note exists with the specified ID",
            content = @Content
        )
    })
    @PutMapping("/{id}")
    public ResponseEntity<Note> updateNote(
            @Parameter(description = "ID of the note to update", required = true, example = "1")
            @PathVariable int id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "Replacement note data; the existing ID is preserved",
                required = true,
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = Note.class)
                )
            )
            @RequestBody Note note) {
        Note updatedNote = noteService.update(id, note);
        if (updatedNote == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(updatedNote, HttpStatus.OK);
    }
}