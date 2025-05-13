package org.yoon.msavoteservice;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.yoon.msavoteservice.kafka.KafkaProducer;
import org.yoon.msavoteservice.model.request.VoteInfoReq;
import org.yoon.msavoteservice.model.response.VoteDetailRes;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class VoteService {

    private final KafkaProducer kafkaProducer;
    private final ObjectMapper objectMapper;
    private final VoteRepository voteRepository;

    public VoteDetailRes vote(long userId, VoteInfoReq req) {
        Vote vote = voteRepository.save(Vote.builder()
                .voterId(userId)
                .targetId(req.getTargetId())
                .questionId(req.getQuestionId())
                .createdAt(LocalDateTime.now())
                .build());

        VoteDetailRes voteDetailRes = Vote.to(vote);

        try {
            kafkaProducer.send("validate.question", objectMapper.writeValueAsString(voteDetailRes));
            System.out.println("send vote.created");
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        return voteDetailRes;
    }
}
