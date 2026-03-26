package com.finance.tracker;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping("/api/forum")
@CrossOrigin(origins = "*")
public class ForumController {

    @Autowired
    private ForumRepository forumRepository;

    
    @GetMapping("/posts")
    public List<ForumPost> getPosts() {
        return forumRepository.findAllByOrderByCreatedAtDesc();
    }

    
    @PostMapping("/add")
    public ResponseEntity<?> addPost(@RequestBody ForumPost post) {
        if (post.getContent() == null || post.getContent().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Post content cannot be empty.");
        }
        
        ForumPost savedPost = forumRepository.save(post);
        return ResponseEntity.ok(savedPost);
    }

    
    @PostMapping("/like/{id}")
    public ResponseEntity<?> likePost(@PathVariable Long id) {
        Optional<ForumPost> postOptional = forumRepository.findById(id);
        
        if (postOptional.isPresent()) {
            ForumPost post = postOptional.get();
            post.setLikes(post.getLikes() + 1);
            forumRepository.save(post);
            return ResponseEntity.ok(post);
        } else {
            return ResponseEntity.status(404).body("Post not found");
        }
    }
}