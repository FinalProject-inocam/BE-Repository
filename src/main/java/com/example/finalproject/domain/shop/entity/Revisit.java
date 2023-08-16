package com.example.finalproject.domain.shop.entity;

import com.example.finalproject.domain.auth.entity.User;
import com.example.finalproject.domain.post.entity.Post;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Revisit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_Id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "review_Id", nullable = false)
    private Review review;

    public Revisit(User user, Review review) {
        this.user=user;
        this.review=review;
    }
}
