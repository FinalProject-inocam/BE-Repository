package com.example.finalproject.domain.post.entity;

import com.example.finalproject.global.utils.Timestamped;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor
@Getter
@Setter
public class Comments extends Timestamped {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String comment;

    @Column(nullable = false)
    private String nickname;

    @ManyToOne
    @JoinColumn(name = "post_id", insertable = false, updatable = false)
    private Posts post;

    public Comments(String nickname, Posts post, String comment) {
        this.nickname=nickname;
        this.comment=comment;
        this.post=post;
    }
}
