package com.sharememories.sharememories.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "users")
@NoArgsConstructor
@Getter @Setter
public class User implements UserDetails {

    @Transient
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    public static final String IMAGES_DIRECTORY_PATH = "uploads/pictures/profiles";

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "user_Id")
    private Long id;

    @Email
    @NotBlank
    @Size(min = 8, max = 34)
    @JsonIgnore
    private String username;

    @NotBlank
    @JsonIgnore
    private String password;

    @NotBlank
    @Size(min = 2, max = 16)
    private String firstname;

    @NotBlank
    @Size(min = 2, max = 24)
    private String lastname;

    private String profileImage;
    private LocalDate creationDate = LocalDate.now();
    private boolean accountNonLocked = true;

    @JsonIgnore
    private String role = "ROLE_USER";

    @OneToMany(mappedBy = "creator", cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<Post> posts;

    @OneToMany(mappedBy = "creator", cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<Comment> comments;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "contacts", joinColumns = {
            @JoinColumn(name = "idA")}, inverseJoinColumns = {
            @JoinColumn(name = "idB")
    })
    @JsonIgnore
    private Set<User> contacts = new HashSet<>();

    @ManyToMany(mappedBy = "members")
    @JsonIgnore
    private Set<Group> groupsMember;

    @ManyToMany(mappedBy = "admins")
    @JsonIgnore
    private Set<Group> groupsAdmin;

    public User(String username) {
        this.username = username;
    }

    public String getImagePath() {
        if (profileImage == null) return "/images/default-avatar.png";

        return "/" + IMAGES_DIRECTORY_PATH + "/" + profileImage;
    }

//    public boolean isUserInContacts(long id) {
//        if (this.id == id) return true;
//
//        return contacts.stream()
//                .anyMatch(u -> u.getId() == id);
//    }

    public String getCapitalizedFirstAndLastName() {
        return this.firstname.substring(0, 1).toUpperCase() + this.firstname.substring(1)
                + " " +
                this.lastname.substring(0, 1).toUpperCase() + this.lastname.substring(1);
    }

    @JsonIgnore
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @JsonIgnore
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(() -> "read");
    }

    @JsonIgnore
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @JsonIgnore
    @Override
    public boolean isEnabled() {
        return true;
    }
}
