//package com.example.finalproject.domain.post.entity;
//
//import jakarta.persistence.*;
//import lombok.Getter;
//import org.springframework.data.annotation.CreatedDate;
//import org.springframework.data.annotation.LastModifiedDate;
//import org.springframework.data.jpa.domain.support.AuditingEntityListener;
//import java.time.LocalDateTime;
//
//
//
//@Getter
//@MappedSuperclass
//@EntityListeners(AuditingEntityListener.class)
//public abstract class Timestamped{
//    @CreatedDate
//    @Temporal(TemporalType.TIMESTAMP)
//    @Column(updatable = false)
//    private LocalDateTime  createdat;
//
//
//    @LastModifiedDate
//    @Column
//    @Temporal(TemporalType.TIMESTAMP)
//    private LocalDateTime modifiedat;
//
//}