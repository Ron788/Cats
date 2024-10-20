package ru.vsu.cs.ustinov.cats.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.vsu.cs.ustinov.cats.model.Like;
import ru.vsu.cs.ustinov.cats.model.Publication;

public interface LikeRepository extends JpaRepository<Like, Long> {
    int countLikeByPost(Publication publication);
}
