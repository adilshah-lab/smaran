package com.hinduprayerlock.backend.service;

import com.hinduprayerlock.backend.model.JaapLog;
import com.hinduprayerlock.backend.repository.JaapLogRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class JaapService {

    private final JaapLogRepository repository;

    public JaapService(JaapLogRepository repository) {
        this.repository = repository;
    }

    public void addJaap(String userId, Integer count){

        LocalDate today = LocalDate.now();

        JaapLog log = repository
                .findByUserIdAndJaapDate(userId, today)
                .orElseGet(() -> {

                    JaapLog j = new JaapLog();
                    j.setUserId(userId);
                    j.setJaapDate(today);
                    j.setCount(0);

                    return j;
                });

        log.setCount(log.getCount() + count);

        repository.save(log);
    }

    public Integer getToday(String userId){

        return repository
                .findByUserIdAndJaapDate(userId, LocalDate.now())
                .map(JaapLog::getCount)
                .orElse(0);
    }
}
