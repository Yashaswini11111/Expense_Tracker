package com.finance.tracker;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*") 
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    
    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            return ResponseEntity.badRequest().body("Email already exists!");
        }
        
        if (user.getMonthlyIncome() == null) user.setMonthlyIncome(0.0);
        
        return ResponseEntity.ok(userRepository.save(user));
    }

    
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credentials) {
        String email = credentials.get("email");
        String password = credentials.get("password");

        User user = userRepository.findByEmail(email).orElse(null);
        
        if (user != null && user.getPassword().equals(password)) {
            return ResponseEntity.ok(user);
        }
        
        return ResponseEntity.status(401).body("Invalid email or password");
    }

    @PutMapping("/update-profile")
    public ResponseEntity<?> updateProfile(@RequestBody User user) {
        Optional<User> userData = userRepository.findById(user.getId());

        if (userData.isPresent()) {
            User existingUser = userData.get();
            
            existingUser.setFullName(user.getFullName());
            existingUser.setMonthlyIncome(user.getMonthlyIncome());
            existingUser.setDob(user.getDob()); 
            
            if (user.getProfilePic() != null && !user.getProfilePic().isEmpty()) {
                existingUser.setProfilePic(user.getProfilePic());
            }

            User updatedUser = userRepository.save(existingUser);
            return ResponseEntity.ok(updatedUser);
        } else {
            return ResponseEntity.status(404).body("User not found in database");
        }
    }
}