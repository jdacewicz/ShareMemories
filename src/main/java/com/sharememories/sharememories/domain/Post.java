package com.sharememories.sharememories.domain;

import jakarta.persistence.*;

import java.time.LocalDate;
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
    @OneToMany(cascade = {CascadeType.ALL})
    @OrderBy("id ASC")
    private List<ReactionCounter> reactionsCounters = new ArrayList<>();

    public Post() {
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

    public List<ReactionCounter> getReactionsCounters() {
        return reactionsCounters;
    }

    public void setReactionsCounters(List<ReactionCounter> reactionsCounters) {
        this.reactionsCounters = reactionsCounters;
    }
}
