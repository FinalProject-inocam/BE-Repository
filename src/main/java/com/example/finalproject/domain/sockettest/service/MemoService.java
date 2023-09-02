package com.example.finalproject.domain.sockettest.service;

import com.example.finalproject.domain.sockettest.model.Memo;
import com.example.finalproject.domain.sockettest.model.Message;
import com.example.finalproject.domain.sockettest.repository.MemoRepository;
import com.example.finalproject.domain.sockettest.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
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
