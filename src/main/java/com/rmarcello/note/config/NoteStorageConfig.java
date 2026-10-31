package com.rmarcello.note.config;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import com.rmarcello.note.repository.NoteRepository;
import com.rmarcello.note.service.NoteOperations;
import com.rmarcello.note.service.NoteService;
import com.rmarcello.note.service.NoteServiceDB;

@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(NoteStorageProperties.class)
public class NoteStorageConfig {
    @Bean
    NoteOperations noteOperations(NoteStorageProperties properties, ObjectProvider<NoteRepository> repository,
            Environment environment) {
        String storage = environment.getProperty("notes.storage", "memory");
        if (!"memory".equalsIgnoreCase(storage) && !"postgres".equalsIgnoreCase(storage)) {
            throw new IllegalArgumentException("notes.storage must be memory or postgres");
        }
        return switch (properties.getStorage()) {
            case MEMORY -> new NoteService();
            case POSTGRES -> new NoteServiceDB(repository.getObject());
        };
    }
}
