package com.example.finalproject.domain.post.entity;

import com.example.finalproject.domain.auth.entity.User;
import com.example.finalproject.global.utils.Timestamped;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
public class Reply extends Timestamped {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String reply;

    @Column(nullable = false)
    private String nickname;

    @ManyToOne
    @JoinColumn(name = "comment_id", insertable = false, updatable = false)
    private Comment comment;

    @ManyToOne
    private User user;

    public Reply(Comment comment, String reply, User user) {
        this.reply = reply;
        this.comment = comment;
        this.user = user;
        this.nickname = user.getNickname();
    }
    public void updateComment(String reply) {
        this.reply=reply;
    }
}
