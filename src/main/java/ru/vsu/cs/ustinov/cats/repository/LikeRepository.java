package ru.vsu.cs.ustinov.cats.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.vsu.cs.ustinov.cats.model.Like;
import ru.vsu.cs.ustinov.cats.model.Publication;
import ru.vsu.cs.ustinov.cats.model.User;

@Repository
public interface LikeRepository extends JpaRepository<Like, Long> {
    int countLikeByPost(Publication publication);
    boolean existsByUserAndPost(User user, Publication publication);
    void deleteByUserAndPost(User user, Publication publication);
}
