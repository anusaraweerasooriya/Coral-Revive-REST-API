package com.project.microservices.auth_service.controller;

import com.project.microservices.auth_service.jwt.JwtUtil;
import com.project.microservices.auth_service.model.User;
import com.project.microservices.auth_service.service.UserService;

import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
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
    public Map<String, Object> authenticate(@RequestBody User user) throws Exception {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword())
            );
        } catch (Exception e) {
            throw new Exception("Invalid username or password", e);
        }

        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUsername());
        User responseUser = userService.findByUsername(userDetails.getUsername());
        String token = jwtUtil.generateToken(responseUser);

        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("user", responseUser);

        return response;
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
            String username = jwtUtil.extractUserId(jwtToken);
            return jwtUtil.validateToken(jwtToken, username);
        } catch (Exception e) {
            return false;
        }
    }
}