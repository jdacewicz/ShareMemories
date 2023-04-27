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
import java.util.Set;

@Controller
public class AppController {

    @Value("${contact.mail.receiver}")
    private String mailReceiver;
    private SecurityUserDetailsService userDetailsService;
    private PasswordEncoder passwordEncoder;
    private EmailServiceImpl emailService;

    @Autowired
    public AppController(SecurityUserDetailsService userDetailsService, PasswordEncoder passwordEncoder, EmailServiceImpl emailService) {
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
    }

    @GetMapping("/")
    public String showMainPage(Model model) {
        User user = userDetailsService.getUserByUsername(SecurityContextHolder.getContext()
                        .getAuthentication()
                        .getName())
                .get();
        model.addAttribute("loggedUser", user);
        return "main";
    }

    @GetMapping("/profile/{id}")
    private String showProfilePage(@PathVariable long id, Model model) {
        User loggedUser = userDetailsService.getUserByUsername(SecurityContextHolder.getContext()
                        .getAuthentication()
                        .getName())
                .get();
        Optional<User> user = userDetailsService.getUserById(id);
        if (user.isPresent()) {
            model.addAttribute("profileUser", user.get());
            model.addAttribute("loggedUser", loggedUser);
            return "profile";
        }
        return "error";
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
                             @RequestPart(value = "image", required = false) MultipartFile file) {
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setFirstname(firstname);
        user.setLastname(lastname);

        if (!file.isEmpty()) {
            try {
                String fileName = FileUtils.generateUniqueName(file.getOriginalFilename());
                FileUtils.saveFile(User.IMAGES_DIRECTORY_PATH, fileName, file);
                user.setProfileImage(fileName);
            } catch (IOException e) {
                System.out.println(e);
            }
        }
        userDetailsService.creatUser(user);
        return "redirect:/login";
    }

    @PostMapping("/contact")
    public String sendMail(@RequestPart String name,
                           @RequestPart String email,
                           @RequestPart String phone,
                           @RequestPart String topic,
                           @RequestPart String message,
                           @RequestPart(required = false) MultipartFile file) {
        User user = userDetailsService.getUserByUsername(SecurityContextHolder.getContext()
                        .getAuthentication()
                        .getName())
                .get();
        String content = "Mail: " + user.getUsername() + " | Name: " + name + " | Phone: " + phone + " | Message: " + message;
        emailService.sendMessage(email, mailReceiver, topic, content);

        return "redirect:/";
    }
}
