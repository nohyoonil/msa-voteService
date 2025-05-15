package org.yoon.msavoteservice.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.yoon.msavoteservice.Vote;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class VoteDetailDto {

    private long voteId;
    private long voterId;
    private long targetId;
    private long questionId;
    private boolean opened;
    private LocalDateTime createdAt;

    public static VoteDetailDto from(Vote vote) {
        return VoteDetailDto.builder()
                .voteId(vote.getId())
                .voterId(vote.getVoterId())
                .targetId(vote.getTargetId())
                .questionId(vote.getQuestionId())
                .opened(vote.isOpened())
                .createdAt(vote.getCreatedAt())
                .build();
    }
}
