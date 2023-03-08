package com.sharememories.sharememories.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sharememories.sharememories.util.TimeUtils;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "postId")
    private long id;
    private String content;
    private String image;
    private LocalTime creationTime = LocalTime.now();
    private LocalDate creationDate = LocalDate.now();
    @ManyToMany(mappedBy = "posts")
    @JsonIgnore
    @OrderBy("id ASC")
    private List<Reaction> reactions = new ArrayList<>();

    public Post() {
    }

    public String getElapsedCreationTimeMessage() {
        LocalDateTime creationDateTime = LocalDateTime.of(creationDate, creationTime);

        return TimeUtils.getElapsedTimeMessage(creationDateTime, LocalDateTime.now());
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public LocalTime getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(LocalTime creationTime) {
        this.creationTime = creationTime;
    }

    public LocalDate getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDate creationDate) {
        this.creationDate = creationDate;
    }

    public List<Reaction> getReactions() {
        return reactions;
    }

    public void setReactions(List<Reaction> reactions) {
        this.reactions = reactions;
    }
}
