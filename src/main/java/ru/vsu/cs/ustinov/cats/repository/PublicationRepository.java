package ru.vsu.cs.ustinov.cats.repository;

import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;
import ru.vsu.cs.ustinov.cats.model.Publication;
import ru.vsu.cs.ustinov.cats.model.User;

import java.util.Optional;

public interface PublicationRepository extends JpaRepository<Publication, Long> {
    Page<Publication> findByAuthor(User author, Pageable pageable);
    @Transactional
    Optional<Publication> findByPostId(Long postId);
}
