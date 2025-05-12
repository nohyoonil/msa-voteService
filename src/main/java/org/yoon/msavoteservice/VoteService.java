package org.yoon.msavoteservice;

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
    private final VoteRepository voteRepository;

    public VoteDetailRes vote(long userId, VoteInfoReq req) {
        Vote vote = voteRepository.save(Vote.builder()
                .voterId(userId)
                .targetId(req.getTargetId())
                .questionId(req.getQuestionId())
                .createdAt(LocalDateTime.now())
                .build());

        kafkaProducer.send("vote.created", "test123");
        System.out.println("send vote created");

        return Vote.to(vote);
    }
}
