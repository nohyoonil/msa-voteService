package org.yoon.msavoteservice;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.yoon.msavoteservice.kafka.KafkaProducer;
import org.yoon.msavoteservice.model.dto.VoteDetailDto;
import org.yoon.msavoteservice.model.request.VoteInfoReq;
import org.yoon.msavoteservice.model.response.VoteDetailRes;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class VoteService {

    private final KafkaProducer kafkaProducer;
    private final RedisTemplate<String, String> redisTemplate;
    private final VoteRepository voteRepository;

    public VoteDetailRes vote(long userId, VoteInfoReq req) {
        long questionId = req.getQuestionId();
        kafkaProducer.send("validate.questionId", String.valueOf(questionId));

        if (!checkReplyInRedis("questionId:" + questionId))
            throw new RuntimeException("Invalid questionId: " + questionId);

        long targetId = req.getTargetId();
        kafkaProducer.send("validate.memberId", String.valueOf(targetId));

        if (!checkReplyInRedis("memberId:" + targetId))
            throw new RuntimeException("Invalid targetId: " + targetId);

        Vote vote = voteRepository.save(Vote.builder()
                .voterId(userId)
                .targetId(targetId)
                .questionId(questionId)
                .createdAt(LocalDateTime.now())
                .build());

        return VoteDetailRes.from(vote);
    }

    private boolean checkReplyInRedis(String key) {
        int limit = 30;
        for (int i = 0; i < limit; i++) {
            if (redisTemplate.hasKey(key)) return true;
            try {
                Thread.sleep(100); // 0.1초
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return false;
            }
        }
        return false;
    }
}

