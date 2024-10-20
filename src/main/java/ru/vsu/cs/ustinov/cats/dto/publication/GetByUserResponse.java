package ru.vsu.cs.ustinov.cats.dto.publication;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GetByUserResponse {
    // TODO: rename class
    private Long post_id;
    private String username;
    private String url;
    private String description;
    private int likesCount;
}
