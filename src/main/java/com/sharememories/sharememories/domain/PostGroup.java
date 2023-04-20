package com.sharememories.sharememories.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;

import java.util.Set;

@Entity
public class PostGroup extends Group {

    @OneToMany(mappedBy = "group", cascade = CascadeType.ALL)
    private Set<Post> posts;

    public PostGroup() {
    }

    public PostGroup(String name, User owner, Set<User> members) {
        super(name, owner, Set.of(owner), members);
    }

    public Set<Post> getPosts() {
        return posts;
    }

    public void setPosts(Set<Post> posts) {
        this.posts = posts;
    }
}
