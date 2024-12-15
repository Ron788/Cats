package ru.vsu.cs.ustinov.cats.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.vsu.cs.ustinov.cats.model.Likes;
import ru.vsu.cs.ustinov.cats.model.Post;
import ru.vsu.cs.ustinov.cats.model.User;
import ru.vsu.cs.ustinov.cats.repository.LikesRepository;
import ru.vsu.cs.ustinov.cats.repository.PostsRepository;
import ru.vsu.cs.ustinov.cats.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@AllArgsConstructor
public class LikesService {

    private final LikesRepository likesRepository;
    private final UserRepository userRepository;
    private final PostsRepository postsRepository;

    public boolean like(String username, Long postId) {
        Optional<User> user = userRepository.findByUsername(username);
        if (user.isEmpty()){
            return false;
        }

        Optional<Post> post = postsRepository.getPostByPostId(postId);
        if (post.isEmpty()){
            return false;
        }

        Likes likes = new Likes();
        likes.setUser(user.get());
        likes.setPost(post.get());
        likes.setCreated(LocalDateTime.now());
        likesRepository.save(likes);
        return true;
    }

    public boolean unlike(String username, Long postId) {
        Optional<User> user = userRepository.findByUsername(username);
        if (user.isEmpty()){
            return false;
        }

        Optional<Post> post = postsRepository.getPostByPostId(postId);
        if (post.isEmpty()){
            return false;
        }

        Optional<Likes> likes = likesRepository.findByUserAndPost(user.get(), post.get());
        if (likes.isEmpty()){
            return false;
        }

        likesRepository.delete(likes.get());
        return true;
    }
}
