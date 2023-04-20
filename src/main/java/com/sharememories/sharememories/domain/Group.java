package com.sharememories.sharememories.domain;

import jakarta.persistence.*;

import java.util.Set;

@MappedSuperclass
public class Group {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "groupId")
    private long id;
    private String name;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User owner;
    @ManyToMany
    @JoinTable(
            name = "users_group_admins",
            joinColumns = @JoinColumn(name = "group_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<User> admins;
    @ManyToMany
    @JoinTable(
            name = "users_group_members",
            joinColumns = @JoinColumn(name = "group_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<User> members;

    public Group() {
    }

    public Group(String name, User owner, Set<User> admins, Set<User> members) {
        this.name = name;
        this.owner = owner;
        this.admins = admins;
        this.members = members;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public Set<User> getAdmins() {
        return admins;
    }

    public void setAdmins(Set<User> admins) {
        this.admins = admins;
    }

    public Set<User> getMembers() {
        return members;
    }

    public void setMembers(Set<User> members) {
        this.members = members;
    }
}
