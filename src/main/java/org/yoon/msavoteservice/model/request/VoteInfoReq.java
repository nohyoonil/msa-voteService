package org.yoon.msavoteservice.model.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VoteInfoReq {

    private long targetId;
    private long questionId;
}
