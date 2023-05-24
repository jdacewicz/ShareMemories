package com.sharememories.sharememories.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "groups")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@NoArgsConstructor
@Getter @Setter
public class Group {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "group_Id")
    private long id;

    @NotBlank
    @Size(max = 34)
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

        users.forEach(u -> u.getGroupsMember().add(this));
    }
}
