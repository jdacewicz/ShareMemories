package com.sharememories.sharememories.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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

    @PreRemove
    private void removeReactionFromRelatedEntities() {
        posts.remove(this);
        comments.remove(this);
    }

    public Reaction() {
    }

    public Reaction(int id) {
        this.id = id;
    }

    public Reaction(String name) {
        this.name = name;
    }


    public Reaction(String name, String image) {
        this.name = name;
        this.image = image;
    }

    public Reaction(int id, String name, String image) {
        this.id = id;
        this.name = name;
        this.image = image;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Reaction reaction = (Reaction) o;
        return Objects.equals(name, reaction.name) && Objects.equals(image, reaction.image);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, image);
    }
}
