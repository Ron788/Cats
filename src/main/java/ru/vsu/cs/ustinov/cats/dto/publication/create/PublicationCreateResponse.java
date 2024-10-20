package ru.vsu.cs.ustinov.cats.dto.publication.create;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PublicationCreateResponse {
    private Long post_id;
    private String username;
    private String url;
    private String description;
}
