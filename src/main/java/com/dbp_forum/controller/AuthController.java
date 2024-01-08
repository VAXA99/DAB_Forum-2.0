package com.dbp_forum.controller;


import com.dbp_forum.model.User;
import com.dbp_forum.service.user.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/auth")
public class AuthController {

    private final UserDetailsServiceImpl userService;

    @Autowired
    public AuthController(UserDetailsServiceImpl userService) {
        this.userService = userService;
    }

    @GetMapping("/register")
    public String getRegisterPage() {
        return "register";
    }

    @GetMapping("/login")
    public String getLoginPage() {
        return "login";
    }

    @PostMapping("/register")
    public String register(@RequestParam(value = "username") String username,
                           @RequestParam(value = "password") String password) {
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setRoles("ROLE_USER");

        userService.addUser(user);

        return "redirect:/auth/login";
    }

}
