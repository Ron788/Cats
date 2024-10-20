package ru.vsu.cs.ustinov.cats.service;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.vsu.cs.ustinov.cats.model.Like;
import ru.vsu.cs.ustinov.cats.model.Publication;
import ru.vsu.cs.ustinov.cats.model.User;
import ru.vsu.cs.ustinov.cats.repository.LikeRepository;
import ru.vsu.cs.ustinov.cats.repository.PublicationRepository;
import ru.vsu.cs.ustinov.cats.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.Optional;

@Service
@AllArgsConstructor
public class LikeService {
    UserRepository userRepository;
    PublicationRepository publicationRepository;
    LikeRepository likeRepository;

    public int countLikesByPublication(Publication publication){
        return likeRepository.countLikeByPost(publication);
    }

    public void likePost(Long post_id, String username) {
        Optional<User> user = userRepository.findByUsername(username);
        if (user.isEmpty()){
            throw new RuntimeException("User not found!");
        }

        Optional<Publication> publication = publicationRepository.findByPostId(post_id);
        if (publication.isEmpty()){
            throw new RuntimeException("Publication not found!");
        }

        // TODO: сделать чтоб информация об этом возвращалась
        if (likeRepository.existsByUserAndPost(user.get(), publication.get())){
            throw new RuntimeException("Post already liked!");
        }

        Like like = new Like();
        like.setUser(user.get());
        like.setPost(publication.get());
        like.setLike_date(LocalDateTime.now());

        likeRepository.save(like);
    }

    @Transactional
    public void removeLike(Long post_id, String username) {
        User user = getUser(username);

        Publication publication = getPublication(post_id);

        likeRepository.deleteByUserAndPost(user, publication);
    }

    private User getUser(String username) {
        Optional<User> user = userRepository.findByUsername(username);
        if (user.isEmpty()){
            throw new RuntimeException("User not found!");
        }
        return user.get();
    }

    private Publication getPublication(Long post_id) {
        Optional<Publication> publication = publicationRepository.findByPostId(post_id);
        if (publication.isEmpty()){
            throw new RuntimeException("Publication not found!");
        }
        return publication.get();
    }
}
