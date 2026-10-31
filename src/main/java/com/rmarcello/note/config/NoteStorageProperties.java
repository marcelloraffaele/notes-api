package com.rmarcello.note.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "notes")
public class NoteStorageProperties {
    public enum Storage {
        MEMORY, POSTGRES
    }

    private Storage storage = Storage.MEMORY;

    public Storage getStorage() {
        return storage;
    }

    public void setStorage(Storage storage) {
        if (storage == null) {
            throw new IllegalArgumentException("notes.storage must be memory or postgres");
        }
        this.storage = storage;
    }

    @Override
    public String toString() {
        return "NoteStorageProperties{storage=" + storage + '}';
    }
}
