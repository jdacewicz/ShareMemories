package com.sharememories.sharememories.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "groups")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public class Group {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "groupId")
    private long id;
    private String name;
    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "user_id")
    private User owner;
    @ManyToMany
    @JoinTable(
            name = "users_group_admins",
            joinColumns = @JoinColumn(name = "group_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<User> admins = new HashSet<>();
    @ManyToMany
    @JoinTable(
            name = "users_group_members",
            joinColumns = @JoinColumn(name = "group_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<User> members = new HashSet<>();

    public Group() {
    }

    public Group(String name, User owner) {
        this.name = name;
        this.owner = owner;
    }

    public Group(String name, User owner, Set<User> admins, Set<User> members) {
        this.name = name;
        this.owner = owner;
        this.admins = admins;
        this.members = members;
    }

    public void addAdmin(User user) {
        this.admins.add(user);
        user.getGroupsAdmin().add(this);
    }

    public void removeAdmin(User user) {
        this.admins.remove(user);
        user.getGroupsAdmin().remove(user);
    }

    public void addMember(User user) {
        this.members.add(user);
        user.getGroupsMember().add(this);
    }

    public void removeMember(User user) {
        this.members.remove(user);
        user.getGroupsMember().remove(this);
    }

    public void addMembers(Set<User> users) {
        this.members.addAll(users);

        users.stream()
                .forEach(u -> u.getGroupsMember().add(this));
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
