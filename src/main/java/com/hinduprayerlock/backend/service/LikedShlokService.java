package com.hinduprayerlock.backend.service;

import com.hinduprayerlock.backend.model.LikedShlok;
import com.hinduprayerlock.backend.repository.LikedShlokRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class LikedShlokService {

    private final LikedShlokRepository repository;

    public LikedShlokService(LikedShlokRepository repository) {
        this.repository = repository;
    }

    public void likeShlok(Long userId, Integer shlokId){

        if (!repository.existsByUserIdAndShlokId(userId, shlokId)) {

            LikedShlok like = new LikedShlok();
            like.setUserId(userId);
            like.setShlokId(shlokId);

            repository.save(like);
        }
    }

    public void unlikeShlok(Long userId, Integer shlokId){
        repository.deleteByUserIdAndShlokId(userId, shlokId);
    }

    public List<Integer> getLikedShloks(Long userId){

        return repository.findByUserId(userId)
                .stream()
                .map(LikedShlok::getShlokId)
                .collect(Collectors.toList());
    }
}