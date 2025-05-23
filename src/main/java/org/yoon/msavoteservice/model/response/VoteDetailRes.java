package org.yoon.msavoteservice.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.yoon.msavoteservice.Vote;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class VoteDetailRes {

    private long voteId;
    private long voterId;
    private long targetId;
    private long questionId;
    private boolean opened;
    private LocalDateTime createdAt;

    public static VoteDetailRes from(Vote vote) {
        return VoteDetailRes.builder()
                .voteId(vote.getId())
                .voterId(vote.getVoterId())
                .targetId(vote.getTargetId())
                .opened(vote.isOpened())
                .createdAt(vote.getCreatedAt())
                .build();
    }
}
