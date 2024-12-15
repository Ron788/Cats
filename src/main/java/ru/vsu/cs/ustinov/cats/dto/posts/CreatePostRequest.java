package ru.vsu.cs.ustinov.cats.dto.posts;

import lombok.Data;

@Data
public class CreatePostRequest {
    String title;
    String body;
}
