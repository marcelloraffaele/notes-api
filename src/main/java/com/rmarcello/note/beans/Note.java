package com.rmarcello.note.beans;

import java.util.List;
import jakarta.validation.constraints.*;

public class Note implements Comparable<Note> {
    private long id;
    
    @NotBlank(message = "Title is required")
    @Size(max = 255, message = "Title must not exceed 255 characters")
    private String title;
    
    @NotBlank(message = "Content is required")
    @Size(max = 5000, message = "Content must not exceed 5000 characters")
    private String content;
    
    private List<@Pattern(regexp = "^[a-zA-Z0-9_-]{1,50}$", message = "Label must contain only alphanumeric characters, hyphens, and underscores, max 50 characters") String> labels;
    
    private List<@Pattern(regexp = "^https?://[\\w\\-]+(\\.[\\w\\-]+)+([\\w\\-\\.,@?^=%&:/~\\+#]*[\\w\\-\\@?^=%&/~\\+#])?$", message = "Invalid URL format") String> urls;
    
    @Pattern(regexp = "^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$", message = "Color must be a valid hex color code (e.g., #FF0000 or #F00)")
    private String color;

    public Note(long id, String title, String content, List<String> labels, List<String> urls, String color) { // Pbb5e
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

    @Override
    public String toString() {
        return "Note{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", labels=" + labels +
                ", urls=" + urls +
                ", color='" + color + '\'' + // Pd1a7
                '}';
    }

    @Override
    public int compareTo(Note other) {
        return this.title.compareTo(other.title);
    }
}
