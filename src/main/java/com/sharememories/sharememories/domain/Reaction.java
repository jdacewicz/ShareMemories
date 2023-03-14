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
    @ManyToMany
    @JsonIgnore
    @JoinTable(
            name = "posts_reactions",
            joinColumns = @JoinColumn(name = "reaction_id"),
            inverseJoinColumns = @JoinColumn(name = "post_id")
    )
    private List<Post> posts = new ArrayList<>();

    @ManyToMany
    @JsonIgnore
    @JoinTable(
            name = "comments_reactions",
            joinColumns = @JoinColumn(name = "reaction_id"),
            inverseJoinColumns = @JoinColumn(name = "comment_id")
    )
    private List<Comment> comments = new ArrayList<>();

    public Reaction() {
    }

    public Reaction(int id) {
        this.id = id;
    }

    public String getImagePath() {
        if (image == null) return null;

        return "/" + IMAGES_DIRECTORY_PATH + "/" + image;
    }

    public void addPost(Post post) {
        this.posts.add(post);
        post.getReactions().add(this);
    }

    public void removePost(Post post) {
        this.posts.remove(post);
        post.getReactions().remove(this);
    }

    public void addComment(Comment comment) {
        this.comments.add(comment);
        comment.getReactions().add(this);
    }

    public void removeComment(Comment comment) {
        this.comments.remove(comment);
        comment.getReactions().remove(this);
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
}
