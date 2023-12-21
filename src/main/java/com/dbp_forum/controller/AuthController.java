package com.dbp_forum.controller;

import com.dbp_forum.controller.request.AuthRequest;
import com.dbp_forum.controller.request.SignUpRequest;
import com.dbp_forum.model.User;
import com.dbp_forum.security.JwtService;
import com.dbp_forum.service.user.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin("http://localhost:3000/")
public class AuthController {

    private final UserDetailsServiceImpl userService;

    private final JwtService jwtService;

    private final AuthenticationManager authenticationManager;


    @Autowired
    public AuthController(UserDetailsServiceImpl userService,
                          JwtService jwtService,
                          AuthenticationManager authenticationManager) {
        this.userService = userService;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    @PostMapping("/signUp")
    public ResponseEntity<?> signup(@RequestBody SignUpRequest signUpRequest) throws IOException {
        User user = new User();
        user.setUsername(signUpRequest.getUsername());
        user.setPassword(signUpRequest.getPassword());
        user.setRoles("ROLE_USER");

        userService.addUser(user);

        return ResponseEntity.ok("Success, User Signed Up Successfully");
    }

    @GetMapping("/existsByUsername")
    public ResponseEntity<?> exists_by_username(String username) {
        boolean userExists = userService.existsUserByUsername(username);
        if (userExists) {
            return ResponseEntity.badRequest().body("User with username " + username + " already exists.");
        } else {
            return ResponseEntity.ok("User with username " + username + " does not exists");
        }
    }

    @PostMapping("/signIn")
    public String authenticateAndGetToken(@RequestBody AuthRequest authRequest) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword()));
        if (authentication.isAuthenticated()) {
            Long id = userService.findUserIdByUsername(authRequest.getUsername());
            String roles = userService.findUserRolesByUsername(authRequest.getUsername());
            return jwtService.generateToken(authRequest.getUsername(), id, roles);
        } else {
            throw new UsernameNotFoundException("Invalid user request !");
        }
    }
}

