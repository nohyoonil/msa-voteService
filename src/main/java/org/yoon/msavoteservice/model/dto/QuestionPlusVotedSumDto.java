package org.yoon.msavoteservice.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class QuestionPlusVotedSumDto {

    private long questionId;
    private long memberId;
}
