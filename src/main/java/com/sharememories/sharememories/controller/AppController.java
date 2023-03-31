package com.sharememories.sharememories.controller;

import com.sharememories.sharememories.domain.User;
import com.sharememories.sharememories.service.SecurityUserDetailsService;
import com.sharememories.sharememories.util.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Controller
public class AppController {

    private SecurityUserDetailsService userDetailsService;
    private PasswordEncoder passwordEncoder;

    @Autowired
    public AppController(SecurityUserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/")
    public String showMainPage(Model model) {
        User user = userDetailsService.getUserByUsername(SecurityContextHolder.getContext()
                        .getAuthentication()
                        .getName())
                .get();
        model.addAttribute("user", user);
        return "main";
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


        return "redirect:/";
    }
}
