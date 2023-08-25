package com.example.finalproject.domain.post.entity;

import com.example.finalproject.domain.auth.entity.User;
import com.example.finalproject.domain.post.dto.PostRequestDto;
import com.example.finalproject.global.utils.Timestamped;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Post extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @Column(nullable = false)
    private String nickname;

    @OneToMany
    @JoinColumn(name = "post_id")
    private List<Comment> commentList;

    @OneToMany
    @JoinColumn(name = "image_id")
    private List<Image> imageList = new ArrayList<>();

    @ManyToOne
    private User user;

    public Post(PostRequestDto postRequestDto, User user) {
        this.content = postRequestDto.getContent();
        this.title = postRequestDto.getTitle();
        this.nickname = user.getNickname();
        this.user = user;
    }
    // 일단 나중에 추가해야할거 좋아요
}
