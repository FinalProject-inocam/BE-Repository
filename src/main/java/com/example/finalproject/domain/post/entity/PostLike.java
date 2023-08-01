package com.example.finalproject.domain.post.entity;

import com.example.finalproject.auth.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor
@Getter
@Setter
public class PostLike {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_Id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "post_Id", nullable = false)
    private Posts posts;

    public PostLike(User user,Posts posts){
        this.user=user;
        this.posts=posts;
    }
}
