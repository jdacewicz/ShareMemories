package com.sharememories.sharememories.service;

import com.sharememories.sharememories.domain.User;
import com.sharememories.sharememories.repository.MessageRepository;
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

    private final UserRepository userRepository;
    private final MessageRepository messageRepository;

    @Autowired
    public SecurityUserDetailsService(UserRepository userRepository, MessageRepository messageRepository) {
        this.userRepository = userRepository;
        this.messageRepository = messageRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not present"));
    }

    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public Optional<User> getUserById(long id) {
        return userRepository.findById(id);
    }

    public Optional<String> getUserImageName(long id) {
        return userRepository.findById(id)
                .map(User::getProfileImage);
    }

    public void creatUser(UserDetails user) {
        userRepository.save((User) user);
    }

    public void deleteUser(long id) {
        userRepository.deleteById(id);
    }

    public Optional<Set<User>> getAllContacts(long userId) {
        return userRepository.findById(userId).map(User::getContacts);
    }

    public Set<User> getAllUnknownMessageSenders(User receiver, boolean messageSeen) {
        return messageRepository.findAllSendersByNotInContactsAndMessageSeen(receiver, receiver.getContacts(), messageSeen);
    }

    public Set<User> getUsers(long[] ids) {
        return userRepository.getAllByIdInList(ids);
    }

    public Optional<User> addUserToFriendsList(User loggedinUser, long addedUserId) {
        Optional<User> addedUser = userRepository.findById(addedUserId);
        if (addedUser.isPresent() && !loggedinUser.getId().equals(addedUserId)) {

            loggedinUser.getContacts().add(addedUser.get());
            return Optional.of(userRepository.save(loggedinUser));
        }
        return Optional.empty();
    }
}
