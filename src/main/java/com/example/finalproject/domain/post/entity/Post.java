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

    @Column(nullable = false)
    private String category;

    @Column
    private Long view = 0L;

    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumn(name = "post_id")
    @OrderBy("createdAt desc")
    private List<Comment> commentList;

    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumn(name = "image_id")
    private List<Image> imageList = new ArrayList<>();

    @OneToMany(mappedBy = "post")
    private List<PostLike> postLikes = new ArrayList<>();

    @ManyToOne
    private User user;

    public Post(PostRequestDto postRequestDto, User user) {
        this.content = postRequestDto.getContent();
        this.title = postRequestDto.getTitle();
        this.nickname = user.getNickname();
        this.category = postRequestDto.getCategory();
        this.user = user;
    }

    public void viewCount() {
        this.view++;
    }
}
