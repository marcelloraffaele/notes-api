package com.rmarcello.note.beans;

import java.time.LocalDateTime;
import java.util.List;

public class Note implements Comparable<Note> {
    private long id;
    private String title;
    private String content;
    private List<String> labels;
    private List<String> urls;
    private String color; // P9ed3
    private LocalDateTime creationDate;

    public Note(long id, String title, String content, List<String> labels, List<String> urls, String color) { // Pbb5e
        this(id, title, content, labels, urls, color, null);
    }

    public Note(long id, String title, String content, List<String> labels, List<String> urls, String color, LocalDateTime creationDate) {
        this.id = id;
        this.title = title;
        this.content = content;
        if(labels == null) {
            this.labels = List.of();
        } else {
            this.labels = labels;
        }
        if(urls == null) {
            this.urls = List.of();
        } else {
            this.urls = urls;
        }
        this.color = color; // Pbb5e
        this.creationDate = creationDate;
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
        this.labels = labels;
    }

    public List<String> getUrls() {
        return urls;
    }

    public void setUrls(List<String> urls) {
        this.urls = urls;
    }

    public String getColor() { // Pab1e
        return color;
    }

    public void setColor(String color) { // Pab1e
        this.color = color;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    @Override
    public String toString() {
        return "Note{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", labels=" + labels +
                ", urls=" + urls +
                ", color='" + color + '\'' + // Pd1a7
                (creationDate != null ? ", creationDate=" + creationDate : "") +
                '}';
    }

    @Override
    public int compareTo(Note other) {
        return this.title.compareTo(other.title);
    }
}
