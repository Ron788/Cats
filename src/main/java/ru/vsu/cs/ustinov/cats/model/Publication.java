package ru.vsu.cs.ustinov.cats.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "posts")
@Data
public class Publication {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long postId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId", nullable = false)
    private User author;

    @Column(nullable = false)
    private String url;

    private String description;

    @Column(nullable = false)
    private LocalDateTime createdAt;
}
