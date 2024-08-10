package com.project.microservices.auth_service.controller;

import com.project.microservices.auth_service.jwt.JwtUtil;
import com.project.microservices.auth_service.model.User;
import com.project.microservices.auth_service.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth-service")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private UserService userService;

    @PostMapping("/authenticate")
    public String authenticate(@RequestBody User user) throws Exception {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword())
            );
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }

        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUsername());
        User responseUser = userService.findByUsername(userDetails.getUsername());
        return jwtUtil.generateToken(responseUser);
    }

    @PostMapping("/register")
    public User register(@RequestBody User user) throws Exception {
        User response;
        try {
            response = userService.saveUser(user);
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }

        return response;
    }

    @GetMapping("/validate-token")
    public boolean validateToken(@RequestHeader("Authorization") String token) {
        try {
            String jwtToken = token.substring(7);
            String username = jwtUtil.extractUsername(jwtToken);
            return jwtUtil.validateToken(jwtToken, username);
        } catch (Exception e) {
            return false;
        }
    }
}