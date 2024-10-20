package ru.vsu.cs.ustinov.cats.controller;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.vsu.cs.ustinov.cats.dto.publication.GetByUserResponse;
import ru.vsu.cs.ustinov.cats.dto.publication.create.PublicationCreateRequest;
import ru.vsu.cs.ustinov.cats.dto.publication.create.PublicationCreateResponse;
import ru.vsu.cs.ustinov.cats.model.Publication;
import ru.vsu.cs.ustinov.cats.service.PublicationService;

import java.security.Principal;

@AllArgsConstructor
@RestController
@RequestMapping("/api/posts/")
public class PublicationController {
    private final PublicationService publicationService;

    @PostMapping("create")
    public ResponseEntity<?> createPublication(@RequestBody PublicationCreateRequest publicationCreateRequest, Principal principal) {
        String username = principal.getName();
        Publication publication = publicationService.createPublication(publicationCreateRequest, username);
        return ResponseEntity.ok(new PublicationCreateResponse(publication.getPost_id(), publication.getAuthor().getUsername(),
                publication.getUrl(), publication.getDescription()));
    }

    @GetMapping("user/{username}")
    public ResponseEntity<?> getPublicationByAuthor(@PathVariable String username,
                                                    @RequestParam(defaultValue = "0") int page,
                                                    @RequestParam(defaultValue = "10") int size) {

        if (size < 1 || size > 20) {
            return ResponseEntity.badRequest().body("Size must be between 1 and 20");
        }

        Page<GetByUserResponse> posts = publicationService.getPublicationByAuthor(username, page, size);
        return ResponseEntity.ok().body(posts);
    }
}
