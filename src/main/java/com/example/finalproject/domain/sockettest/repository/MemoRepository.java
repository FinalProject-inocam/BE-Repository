package com.example.finalproject.domain.sockettest.repository;

import com.example.finalproject.domain.sockettest.model.Memo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemoRepository extends JpaRepository<Memo, Long> {

    Optional<Memo> findByRoom(String roomName);
}