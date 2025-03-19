package org.example.lawngarden.service;

import org.example.lawngarden.entity.Post;
import org.example.lawngarden.entity.User;
import org.example.lawngarden.repository.PostRepository;
import org.example.lawngarden.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.util.stream.Collectors;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@Service
public class PostService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;


    public PostService(UserRepository userRepository, PostRepository postRepository) {
        this.userRepository = userRepository;
        this.postRepository = postRepository;
    }

    public List<Post> getAllPosts() {
        return postRepository.findAll();
    }

    public Post getPostById(Long id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다."));
    }

    public void createPost(Post post, MultipartFile image) throws IOException {
        if (!image.isEmpty()) {
            String uploadDir = "src/main/resources/static/uploads/";
            File dir = new File(uploadDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            String filePath = uploadDir + image.getOriginalFilename();
            image.transferTo(new File(filePath));
            post.setImagePath("/uploads/" + image.getOriginalFilename());
        }
        post.setCreatedDate(LocalDate.now()); // 오늘 날짜 저장
        postRepository.save(post);
    }

    // 특정 날짜에 글을 작성한 사용자 목록 조회
    public List<Post> getPostsByDate(LocalDate date) {
        return postRepository.findByCreatedDate(date);
    }

    // 벌칙 대상자 찾기 (오늘 글을 작성하지 않은 사용자 목록 조회)
    public List<User> getPenaltyUsers() {
        LocalDate today = LocalDate.now();
        List<User> allUsers = userRepository.findAll();
        Collectors Collectors;
        List<User> usersWhoPostedToday = postRepository.findByCreatedDate(today)
                .stream()
                .map(Post::getUser) // Post 객체에서 User 객체 추출
                .distinct() // 중복 제거
                .collect(java.util.stream.Collectors.toList()); // 리스트로 변환

        return allUsers.stream()
                .filter(user -> !usersWhoPostedToday.contains(user))
                .collect(java.util.stream.Collectors.toList());
    }

}
