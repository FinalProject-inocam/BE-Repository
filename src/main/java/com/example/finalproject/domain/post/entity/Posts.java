package com.example.finalproject.domain.post.entity;

import com.example.finalproject.domain.post.dto.PostRequestDto;
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
public class Posts extends Timestamped{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private String nickname;

    @OneToMany
    @JoinColumn(name = "post_id") // users 테이블에 food_id 컬럼
    private List<Comments> commentList;

    @OneToMany
    @JoinColumn(name = "image_id",nullable = true)
    private List<Image> imageList=new ArrayList<>();

    public Posts(PostRequestDto postRequestDto,String nickname) {
        this.content=postRequestDto.getContent();
        this.title=postRequestDto.getTitle();
        this.nickname=nickname;
    }
    // 일단 나중에 추가해야할거 좋아요
}
