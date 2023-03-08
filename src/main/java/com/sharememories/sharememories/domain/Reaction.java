package com.sharememories.sharememories.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "reactions")
public class Reaction {

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
    private Set<Post> posts = new HashSet<>();

    public Reaction() {
    }

    public void addPost(Post post) {
        this.posts.add(post);
        post.getReactions().add(this);
    }

    public void removePost(Post post) {
        this.posts.remove(post);
        post.getReactions().remove(this);
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

    public Set<Post> getPosts() {
        return posts;
    }

    public void setPosts(Set<Post> posts) {
        this.posts = posts;
    }
}
