package ru.vsu.cs.ustinov.cats.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.vsu.cs.ustinov.cats.model.Post;
import ru.vsu.cs.ustinov.cats.model.User;
import ru.vsu.cs.ustinov.cats.repository.PostsRepository;
import ru.vsu.cs.ustinov.cats.repository.UserRepository;

import java.util.Optional;

@Service
@AllArgsConstructor
public class PostsService {
    private final UserRepository userRepository;
    private final PostsRepository postsRepository;

    public boolean newPost(User user, String title, String body) {
        Post post = new Post();
        post.setTitle(title);
        post.setBody(body);
        post.setUser(user);
        postsRepository.save(post);

        return true;
    }

    public boolean deletePost(String username, Long postId) {
        Optional<User> user = userRepository.findByUsername(username);
        if (user.isEmpty()) {
            return false;
        }

        Optional<Post> post = postsRepository.getPostByPostId(postId);
        if (post.isEmpty()) {
            return false;
        }

        if (!post.get().getUser().getUsername().equals(user.get().getUsername())) {
            return false;
        }

        postsRepository.delete(post.get());
        return true;
    }

    public Post getPost(Long postId) {
        Optional<Post> post = postsRepository.getPostByPostId(postId);
        return post.orElse(null);
    }
}
