package ru.vsu.cs.ustinov.cats.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.vsu.cs.ustinov.cats.model.Post;

import java.util.Optional;

public interface PostsRepository extends JpaRepository<Post, Long> {
    public Optional<Post> getPostByPostId(Long post_id);
}
