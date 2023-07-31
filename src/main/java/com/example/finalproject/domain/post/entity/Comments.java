package com.example.finalproject.domain.post.entity;

import jakarta.persistence.*;

@Entity
public class Comments {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String comment;

//    @Column(nullable = false)
//    private String created_At;
//    @Column(nullable = false)
//    private String modified_At;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Posts post;
}
