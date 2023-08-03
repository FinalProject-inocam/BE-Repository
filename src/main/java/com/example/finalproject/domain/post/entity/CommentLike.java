package com.example.finalproject.domain.post.entity;

import com.example.finalproject.domain.auth.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class CommentLike {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_Id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "post_Id", nullable = false)
    private Comments comments;

    public CommentLike(User user, Comments comments) {
        this.user = user;
        this.comments = comments;
    }
}
