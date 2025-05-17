package org.yoon.msavoteservice;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.yoon.msavoteservice.kafka.KafkaProducer;
import org.yoon.msavoteservice.model.dto.OpenInfoDto;
import org.yoon.msavoteservice.model.dto.QuestionPlusVotedSumDto;
import org.yoon.msavoteservice.model.dto.VoteDetailDto;
import org.yoon.msavoteservice.model.request.VoteInfoReq;
import org.yoon.msavoteservice.model.response.VoteDetailRes;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class VoteService {

    private final KafkaProducer kafkaProducer;
    private final ObjectMapper objectMapper;
    private final RedisTemplate<String, String> redisTemplate;
    private final VoteRepository voteRepository;

    //투표하기(생성)
    public VoteDetailRes vote(long userId, VoteInfoReq req) {
        long questionId = req.getQuestionId();
        kafkaProducer.send("validate.questionId", String.valueOf(questionId));
        checkReplyInRedis("questionId:" + questionId);

        long targetId = req.getTargetId();
        kafkaProducer.send("validate.memberId", String.valueOf(targetId));
        checkReplyInRedis("memberId:" + targetId);

        Vote vote = voteRepository.save(Vote.builder()
                .voterId(userId)
                .targetId(targetId)
                .questionId(questionId)
                .createdAt(LocalDateTime.now())
                .build());

        VoteDetailDto dto = VoteDetailDto.from(vote);

        try {
            kafkaProducer.send("vote.created", objectMapper.writeValueAsString(dto)); // 알림 요청 발행
            kafkaProducer.send("member.plusVoteSum", String.valueOf(userId));
            kafkaProducer.send("member.plusVotedSum", String.valueOf(targetId));
            kafkaProducer.send("question.plusVotedSum",
                    objectMapper.writeValueAsString(new QuestionPlusVotedSumDto(questionId, vote.getVoterId())));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return VoteDetailRes.from(vote);
    }

    //투표자 누군지 알아내기
    public long open(long memberId, long voteId) {
        Vote vote = voteRepository.findById(voteId).orElseThrow(RuntimeException::new);
        if (vote.isOpened())
            throw new RuntimeException("vote is already opened");
        if (memberId != vote.getTargetId())
            throw new RuntimeException("Invalid memberId: " + memberId);
        try {
            kafkaProducer.send("use.point", objectMapper.writeValueAsString(new OpenInfoDto(memberId, voteId)));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        checkReplyInRedis("memberId:" + memberId + "voteId:" + voteId + "open");

        return vote.getVoterId();
    }

    private void checkReplyInRedis(String key) {
        int limit = 10;
        for (int i = 0; i < limit; i++) {
            if (redisTemplate.hasKey(key)) return;
            try {
                Thread.sleep(100); // 0.1초
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException(e);
            }
        }
        throw new RuntimeException("Invalid reply");
    }
}

