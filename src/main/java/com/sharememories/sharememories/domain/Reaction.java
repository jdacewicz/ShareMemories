package com.sharememories.sharememories.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "reactions")
public class Reaction {

    @Transient
    public static final String IMAGES_DIRECTORY_PATH = "uploads/reactions";
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "reactionId")
    private int id;
    private String name;
    private String image;
    @ManyToMany(mappedBy = "reactions")
    @JsonIgnore
    private List<Post> posts = new ArrayList<>();

    @ManyToMany(mappedBy = "reactions")
    @JsonIgnore
    private List<Comment> comments = new ArrayList<>();

    public Reaction() {
    }

    public Reaction(int id) {
        this.id = id;
    }

    public Reaction(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getImagePath() {
        if (image == null) return null;

        return "/" + IMAGES_DIRECTORY_PATH + "/" + image;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public List<Post> getPosts() {
        return posts;
    }

    public void setPosts(List<Post> posts) {
        this.posts = posts;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }
}
