package com.sharememories.sharememories.controller;

import com.sharememories.sharememories.domain.User;
import com.sharememories.sharememories.service.EmailServiceImpl;
import com.sharememories.sharememories.service.SecurityUserDetailsService;
import com.sharememories.sharememories.util.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

import static jdk.jshell.spi.ExecutionControl.NotImplementedException;

@Controller
public class AppController {

    @Value("${contact.mail.receiver}")
    private String mailReceiver;

    private final SecurityUserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;
    private final EmailServiceImpl emailService;

    @Autowired
    public AppController(SecurityUserDetailsService userDetailsService, PasswordEncoder passwordEncoder, EmailServiceImpl emailService) {
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
    }

    @GetMapping("/")
    public String showMainPage(Model model) throws NotImplementedException {
        Optional<User> user = getLoggedUser();
        if (user.isEmpty()) {
            throw new NotImplementedException("User not logged");
        }
        model.addAttribute("loggedUser", user);
        return "main";
    }

    @GetMapping("/profile/{id}")
    private String showProfilePage(@PathVariable long id, Model model) throws NotImplementedException {
        Optional<User> loggedUser = getLoggedUser();
        if (loggedUser.isEmpty()) {
            throw new NotImplementedException("User not logged");
        }

        Optional<User> user = userDetailsService.getUserById(id);
        if (user.isPresent()) {
            model.addAttribute("profileUser", user.get());
            model.addAttribute("loggedUser", loggedUser);
            return "profile";
        }
        throw new NotImplementedException("User " + id + " not found.");
    }

    @GetMapping("/login")
    public String showLoginPage() {
        return "login";
    }

    @GetMapping("/register")
    public String showRegisterPage() {
        return "register";
    }

    @PostMapping("/register")
    public String createUser(@RequestPart String username,
                             @RequestPart String password,
                             @RequestPart String firstname,
                             @RequestPart String lastname,
                             @RequestPart(value = "image", required = false) MultipartFile file) throws NotImplementedException {
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setFirstname(firstname);
        user.setLastname(lastname);

        if (!file.isEmpty() && file.getOriginalFilename() != null) {
            try {
                String fileName = FileUtils.generateUniqueName(file.getOriginalFilename());
                FileUtils.saveFile(User.IMAGES_DIRECTORY_PATH, fileName, file);
                user.setProfileImage(fileName);
            } catch (IOException e) {
                throw new NotImplementedException("User not logged");
            }
        }
        userDetailsService.creatUser(user);
        return "redirect:/login";
    }

    @PostMapping("/contact")
    public String sendMail(@RequestPart String firstname,
                           @RequestPart String lastname,
                           @RequestPart String email,
                           @RequestPart(required = false) String phone,
                           @RequestPart String topic,
                           @RequestPart String message) throws NotImplementedException {
                          // @RequestPart(required = false) MultipartFile file) {
        Optional<User> user = getLoggedUser();
        if (user.isEmpty()) {
            throw new NotImplementedException("User not logged");
        }

        String content = "Username: " + user.get().getUsername() + " | Name: " + firstname + " " + lastname + " | Phone: " + phone + " | Message: " + message;
        emailService.sendMessage(email, mailReceiver, topic, content);

        return "redirect:/";
    }

    private Optional<User> getLoggedUser() {
        return  userDetailsService.getUserByUsername(SecurityContextHolder.getContext()
                        .getAuthentication()
                        .getName());
    }
}
