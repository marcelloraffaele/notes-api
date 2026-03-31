package com.rmarcello.note.exception;

public class NoteAlreadyPresentException extends RuntimeException {

    public NoteAlreadyPresentException(String message) {
        super(message);
    }

}
