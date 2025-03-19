package org.example.lawngarden.controller;

import org.example.lawngarden.entity.Post;
import org.example.lawngarden.entity.User;
import org.example.lawngarden.service.PostService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.example.lawngarden.auth.UserDetailsImpl;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/posts")
public class PostController {
    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping
    public String listPosts(Model model) {
        model.addAttribute("posts", postService.getAllPosts());
        return "post_list";
    }

    @GetMapping("/new")
    public String showPostForm(Model model) {
        model.addAttribute("post", new Post());
        return "post_form";
    }

    @PostMapping
    public String createPost(@ModelAttribute Post post,
                             @RequestParam("image") MultipartFile image,
                             @AuthenticationPrincipal UserDetailsImpl userDetails) throws IOException {
        post.setUser(userDetails.getUser());
        postService.createPost(post, image);
        return "redirect:/posts";
    }

    @GetMapping("/{id}")
    public String viewPost(@PathVariable Long id, Model model) {
        model.addAttribute("post", postService.getPostById(id));
        return "post_detail";
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/penalties")
    public String getPenaltyUsers(Model model) {
        List<User> penaltyUsers = postService.getPenaltyUsers();
        model.addAttribute("penaltyUsers", penaltyUsers);
        return "penalty_list"; // ✅ penalty_list.html로 연결
    }
}
