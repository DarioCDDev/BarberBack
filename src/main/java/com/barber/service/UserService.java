package com.barber.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.barber.entities.User;
import com.barber.repository.UserRepository;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserService {
    
    @Autowired
    private UserRepository userRepository;

    // Create a new User
    public User createUser(User user) {
        return userRepository.save(user);
    }

    // Read a User by ID
    public User getUserById(Long id) {
        return userRepository.findById(id).get();
    }

    // Read all Users
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // Update an existing User
    public User updateUser(Long id, User user) {
    	
    	Optional<User> _user = userRepository.findById(id);
        if (_user.isPresent()) {
        	User currentUser = _user.get();
            if(currentUser.getName() != null) {
            	currentUser.setName(user.getName());
            }
            if(currentUser.getEmail() != null) {
            	currentUser.setName(user.getEmail());
            }
            if(currentUser.getPhone() != null) {
            	currentUser.setName(user.getPhone());
            }
           
            
            return userRepository.save(currentUser);
        }
        return null; // or throw an exception if preferred
    }
    
    public User findUserById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
    }

    // Delete a User by ID
    public User deleteUser(Long id) { 
    	User user = userRepository.findById(id).get();
        userRepository.deleteById(id);
        return user;
    }

    // Find User by email
    public Optional<User> findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }
}
