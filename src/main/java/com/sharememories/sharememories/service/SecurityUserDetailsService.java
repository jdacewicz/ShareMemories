package com.sharememories.sharememories.service;

import com.sharememories.sharememories.domain.User;
import com.sharememories.sharememories.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;

@Service
public class SecurityUserDetailsService implements UserDetailsService {

    private UserRepository repository;

    @Autowired
    public SecurityUserDetailsService(UserRepository repository) {
        this.repository = repository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = repository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not present"));
        return user;
    }

    public Optional<User> getUserByUsername(String username) {
        return repository.findByUsername(username);
    }

    public Optional<User> getUserById(long id) {
        return repository.findById(id);
    }

    public Optional<String> getUserImageName(long id) {
        return repository.findById(id)
                .map(u -> u.getProfileImage());
    }

    public void creatUser(UserDetails user) {
        repository.save((User) user);
    }

    public void deleteUser(long id) {
        repository.deleteById(id);
    }

    public Optional<Set<User>> getAllContacts(long userId) {
        return repository.findById(userId).map(u -> u.getContacts());
    }

    public Optional<User> addUserToFriendsList(User loggedinUser, long addedUserId) {
        Optional<User> addedUser = repository.findById(addedUserId);
        if (addedUser.isPresent() && !loggedinUser.getId().equals(addedUserId)) {

            loggedinUser.getContacts().add(addedUser.get());
            return Optional.of(repository.save(loggedinUser));
        }
        return Optional.empty();
    }
}
