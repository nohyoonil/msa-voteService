package org.yoon.msavoteservice;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.yoon.msavoteservice.model.response.VoteDetailRes;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Vote {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private long voterId;
    private long targetId;
    private long questionId;
    private boolean opened;
    private LocalDateTime createdAt;

    public static VoteDetailRes to(Vote vote) {
        return VoteDetailRes.builder()
                .voteId(vote.getId())
                .voterId(vote.getVoterId())
                .targetId(vote.getTargetId())
                .questionId(vote.getQuestionId())
                .createdAt(vote.getCreatedAt())
                .build();
    }
}
