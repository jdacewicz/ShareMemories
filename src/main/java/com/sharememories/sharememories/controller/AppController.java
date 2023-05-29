package com.sharememories.sharememories.controller;

import com.sharememories.sharememories.domain.User;
import com.sharememories.sharememories.exception.NotMatchException;
import com.sharememories.sharememories.exception.NotUniqueException;
import com.sharememories.sharememories.service.EmailServiceImpl;
import com.sharememories.sharememories.service.SecurityUserDetailsService;
import com.sharememories.sharememories.util.FileUtils;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

import static com.sharememories.sharememories.util.UserUtils.getLoggedUser;
import static jdk.jshell.spi.ExecutionControl.NotImplementedException;

@Controller
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class AppController {

    @Value("${contact.mail.receiver}")
    private String mailReceiver;

    private final SecurityUserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;
    private final EmailServiceImpl emailService;

    @GetMapping("/")
    public String showMainPage(Model model) {
        User user = getLoggedUser(userDetailsService);
        model.addAttribute("loggedUser", user);

        return "main";
    }

    @GetMapping("/profile/{id}")
    private String showProfilePage(@PathVariable long id, Model model) throws NotImplementedException {
        User loggedUser = getLoggedUser(userDetailsService);

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
                             @RequestPart @Size(min = 8, max = 24) String password,
                             @RequestPart @Size(min = 8, max = 24) String repeatPassword,
                             @RequestPart String firstname,
                             @RequestPart String lastname,
                             @RequestPart(value = "image", required = false) MultipartFile file,
                             Model model) throws IOException {
        if (!userDetailsService.isUsernameUnique(username)) {
            throw new NotUniqueException("This email is already taken. Please choose another one.");
        }
        if (!password.equals(repeatPassword)) {
            throw new NotMatchException("Passwords don't match. Try again.");
        }

        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setFirstname(firstname);
        user.setLastname(lastname);

        if (!file.isEmpty() && file.getOriginalFilename() != null) {
            String fileName = FileUtils.generateUniqueName(file.getOriginalFilename());
            FileUtils.saveFile(User.IMAGES_DIRECTORY_PATH, fileName, file);
            user.setProfileImage(fileName);
        }
        userDetailsService.createUser(user);
        model.addAttribute("successMessage",
                "You have successfully created account! You can now log in.");
        return "login";
    }

    @PostMapping("/contact")
    public String sendMail(@RequestPart String firstname,
                           @RequestPart String lastname,
                           @RequestPart String email,
                           @RequestPart(required = false) String phone,
                           @RequestPart String topic,
                           @RequestPart String message) {
                          // @RequestPart(required = false) MultipartFile file) {
        User user = getLoggedUser(userDetailsService);

        String content = "Username: " + user.getUsername() + " | Name: " + firstname + " " + lastname + " | Phone: " + phone + " | Message: " + message;
        emailService.sendMessage(email, mailReceiver, topic, content);

        return "redirect:/";
    }
}
