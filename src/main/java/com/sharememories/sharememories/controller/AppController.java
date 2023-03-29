package com.sharememories.sharememories.controller;

import com.sharememories.sharememories.domain.User;
import com.sharememories.sharememories.service.SecurityUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

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
    public String showMainSite() {
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
    public String createUser(@RequestParam Map<String, String> body ) {
        User user = new User();
        user.setUsername(body.get("username"));
        user.setPassword(passwordEncoder.encode(body.get("password")));

        userDetailsService.creatUser(user);
        return "redirect:/login";
    }
}
