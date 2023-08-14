package br.com.pb.compass.challenge3.controller;

import br.com.pb.compass.challenge3.dto.PostDto;
import br.com.pb.compass.challenge3.exceptions.PostNotFoundException;
import br.com.pb.compass.challenge3.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/posts")
public class PostController {

    private final PostService postService;

    @Autowired
    public PostController(PostService postService) {
        this.postService = postService;
    }


    @PostMapping("/{postId}")
    public ResponseEntity<String> createAndProcessPost(@PathVariable Long postId) {
        postService.processPost(postId);
        return ResponseEntity.ok("Post is being processed");
    }


    @PutMapping("/{postId}")
    public ResponseEntity<PostDto> reprocessPost(@PathVariable Long postId) {
        PostDto reprocessPost = postService.reprocessPost(postId);
        return ResponseEntity.ok(reprocessPost);
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<String> togglePostStatus(@PathVariable Long postId) {
        postService.togglePostStatus(postId);
        return ResponseEntity.ok("Post status toggled successfully.");
    }

    @GetMapping("/{postId}")
    public ResponseEntity<PostDto> getPostWithHistory(@PathVariable Long postId) {
        return ResponseEntity.ok(postService.getPostWithHistory(postId));
    }


    @GetMapping
    public ResponseEntity<List<PostDto>> getAllPosts() {
        List<PostDto> posts = postService.getAllPosts();
        return ResponseEntity.ok(posts);
    }




}
