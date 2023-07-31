package com.example.finalproject.domain.post.entity;

import com.example.finalproject.domain.post.dto.PostRequestDto;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Posts{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private String nickname;

//    @Column(updatable = false)
//    @CreatedDate
//    @Temporal(TemporalType.TIMESTAMP)
//    private LocalDateTime createdDate;
//
//    @LastModifiedDate
//    @Temporal(TemporalType.TIMESTAMP)
//    private LocalDateTime modifiedDate;

//    @OneToMany
//    @JoinColumn(name = "post_id", nullable = false)
//    private List<Comments> commentsList=new ArrayList<>();

    @OneToMany(mappedBy = "post", fetch = FetchType.EAGER, cascade = CascadeType.REMOVE)
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
