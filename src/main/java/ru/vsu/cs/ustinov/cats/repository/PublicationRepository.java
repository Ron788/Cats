package ru.vsu.cs.ustinov.cats.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.vsu.cs.ustinov.cats.model.Publication;
import ru.vsu.cs.ustinov.cats.model.User;

public interface PublicationRepository extends JpaRepository<Publication, Long> {
    Page<Publication> findByAuthor(User author, Pageable pageable);
}
