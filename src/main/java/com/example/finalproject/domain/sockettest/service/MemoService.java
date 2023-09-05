package com.example.finalproject.domain.sockettest.service;

import com.example.finalproject.domain.sockettest.entity.Memo;
import com.example.finalproject.domain.sockettest.repository.MemoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class MemoService {

    private final MemoRepository memoRepository;


    public Optional<Memo> getMemo(String room) {
        return memoRepository.findByRoom(room);
    }

    public Memo saveMemo(Memo memo) {
        return memoRepository.save(memo);
    }

}
