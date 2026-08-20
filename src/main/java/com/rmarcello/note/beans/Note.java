package com.rmarcello.note.beans;

import java.util.List;

import org.hibernate.validator.constraints.URL;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class Note implements Comparable<Note> {
    private long id;
    private String title;
    private String content;
    private List<String> labels;
    private List<String> urls;
    private String color; // P9ed3
    @Size(max = 32, message = "Emoji must not exceed 32 characters")
    @Pattern(regexp = "^(?:[\\x{1F000}-\\x{1FAFF}\\x{2600}-\\x{27BF}][\\uFE0F\\p{Sk}]?)(?:\\u200D[\\x{1F000}-\\x{1FAFF}\\x{2600}-\\x{27BF}][\\uFE0F\\p{Sk}]?)*$",
            message = "Emoji must be a valid emoji")
    @Schema(description = "Optional emoji associated with the note", example = "💡")
    private String emoji;
    @Size(max = 2048, message = "Image URL must not exceed 2048 characters")
    @URL(message = "Image URL must be a valid URL")
    @Pattern(regexp = "^https?://.*$", message = "Image URL must use HTTP(S)")
    @Schema(description = "Optional HTTP(S) image URL associated with the note", example = "https://example.com/image.jpg")
    private String imageUrl;

    public Note() {
    }

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

    public Note(long id, String title, String content, List<String> labels, List<String> urls, String color,
            String emoji, String imageUrl) {
        this(id, title, content, labels, urls, color);
        this.emoji = emoji;
        this.imageUrl = imageUrl;
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

    public String getEmoji() {
        return emoji;
    }

    public void setEmoji(String emoji) {
        this.emoji = emoji;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
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
                ", emoji='" + emoji + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                '}';
    }

    @Override
    public int compareTo(Note other) {
        return this.title.compareTo(other.title);
    }
}
