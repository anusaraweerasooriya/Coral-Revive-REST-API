package com.project.microservices.auth_service.service;

import com.project.microservices.auth_service.model.User;
import com.project.microservices.auth_service.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;


    @Autowired
    @Lazy
    private PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return new org.springframework.security.core.userdetails.User(user.getUsername(),
                user.getPassword(), user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.name()))
                .collect(Collectors.toList()));
    }

    public User saveUser(User user) throws Exception {

        Optional<User> existingUserByUsername = userRepository.findByUsername(user.getUsername());
        if (existingUserByUsername.isPresent()) {
            throw new Exception("Username already exists");
        }

        Optional<User> existingUserByNic = userRepository.findByNic(user.getNic());
        if (existingUserByNic.isPresent()) {
            throw new Exception("NIC already exists");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public User findByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    public User followUser(String userId, String userIdToFollow) {
        Optional<User> userOptional = userRepository.findById(userId);
        Optional<User> userToFollowOptional = userRepository.findById(userIdToFollow);

        if (userOptional.isPresent() && userToFollowOptional.isPresent()) {
            User user = userOptional.get();
            User userToFollow = userToFollowOptional.get();

            if (user.getFollowing().add(userToFollow.getId())) { 
                userToFollow.getFollowers().add(user.getId());
                userRepository.save(userToFollow);
                return userRepository.save(user); 
            }
        }
        return null; 
    }

    // Unfollow a user
    public User unfollowUser(String userId, String userIdToUnfollow) {
        Optional<User> userOptional = userRepository.findById(userId);
        Optional<User> userToUnfollowOptional = userRepository.findById(userIdToUnfollow);

        if (userOptional.isPresent() && userToUnfollowOptional.isPresent()) {
            User user = userOptional.get();
            User userToUnfollow = userToUnfollowOptional.get();

            if (user.getFollowing().remove(userToUnfollow.getId())) { 
                userToUnfollow.getFollowers().remove(user.getId());
                userRepository.save(userToUnfollow);
                return userRepository.save(user); 
            }
        }
        return null; 
    }

    public Optional<User> getUserById(String userId) {
        return userRepository.findById(userId);
    }

}
