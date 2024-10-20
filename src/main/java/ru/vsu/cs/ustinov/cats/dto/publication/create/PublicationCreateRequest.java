package ru.vsu.cs.ustinov.cats.dto.publication.create;

import lombok.Data;

@Data
public class PublicationCreateRequest {
    private String url;
    private String description;
}
