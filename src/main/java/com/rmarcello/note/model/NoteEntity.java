package com.rmarcello.note.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.Table;

@Entity
@Table(name = "notes")
public class NoteEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(columnDefinition = "text")
    private String title;

    @Column(columnDefinition = "text")
    private String content;

    @ElementCollection
    @CollectionTable(name = "note_labels", joinColumns = @JoinColumn(name = "note_id"))
    @OrderColumn(name = "position")
    @Column(name = "label", columnDefinition = "text")
    private List<String> labels = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "note_urls", joinColumns = @JoinColumn(name = "note_id"))
    @OrderColumn(name = "position")
    @Column(name = "url", columnDefinition = "text")
    private List<String> urls = new ArrayList<>();

    @Column(columnDefinition = "text")
    private String color;

    public NoteEntity() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public List<String> getLabels() {
        return labels;
    }

    public void setLabels(List<String> labels) {
        this.labels = labels == null ? new ArrayList<>() : new ArrayList<>(labels);
    }

    public List<String> getUrls() {
        return urls;
    }

    public void setUrls(List<String> urls) {
        this.urls = urls == null ? new ArrayList<>() : new ArrayList<>(urls);
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    @Override
    public String toString() {
        return "NoteEntity{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", labels=" + labels +
                ", urls=" + urls +
                ", color='" + color + '\'' +
                '}';
    }
}
