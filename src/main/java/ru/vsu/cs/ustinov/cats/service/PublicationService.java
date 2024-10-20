package ru.vsu.cs.ustinov.cats.service;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.vsu.cs.ustinov.cats.dto.publication.GetByUserResponse;
import ru.vsu.cs.ustinov.cats.dto.publication.create.PublicationCreateRequest;
import ru.vsu.cs.ustinov.cats.model.Publication;
import ru.vsu.cs.ustinov.cats.model.User;
import ru.vsu.cs.ustinov.cats.repository.PublicationRepository;

import java.time.LocalDateTime;

@Service
@AllArgsConstructor
public class PublicationService {
    private final PublicationRepository publicationRepository;
    private final UserService userService;
    private final LikeService likeService;

    public Publication createPublication(PublicationCreateRequest publicationCreateRequest, String username) {
        User author = userService.findByUsername(username);
        Publication publication = new Publication();
        publication.setAuthor(author);
        publication.setUrl(publicationCreateRequest.getUrl());
        publication.setDescription(publicationCreateRequest.getDescription());
        publication.setCreatedAt(LocalDateTime.now());

        return publicationRepository.save(publication);
    }

    public Page<GetByUserResponse> getPublicationByAuthor(String username, int page, int size) {
        if (size < 1 || size > 20) {
            throw new IllegalArgumentException("Size must be between 1 and 20");
        }

        Pageable pageable = PageRequest.of(page, size);

        User author = userService.findByUsername(username);
        return publicationRepository.findByAuthor(author, pageable).map(post ->
            new GetByUserResponse(post.getPostId(), post.getAuthor().getUsername(),
                    post.getUrl(), post.getDescription(), likeService.countLikesByPublication(post))
        );
    }
}
