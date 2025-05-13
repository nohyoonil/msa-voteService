package org.yoon.msavoteservice.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

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
}
