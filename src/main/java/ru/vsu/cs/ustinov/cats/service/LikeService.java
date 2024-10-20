package ru.vsu.cs.ustinov.cats.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.vsu.cs.ustinov.cats.model.Publication;
import ru.vsu.cs.ustinov.cats.repository.LikeRepository;

@Service
@AllArgsConstructor
public class LikeService {
    LikeRepository likeRepository;

    public int countLikesByPublication(Publication publication){
        return likeRepository.countLikeByPost(publication);
    }
}
