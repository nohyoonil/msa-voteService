package org.yoon.msavoteservice;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.yoon.msavoteservice.model.request.VoteInfoReq;
import org.yoon.msavoteservice.model.response.VoteDetailRes;

@RestController
@RequiredArgsConstructor
public class VoteController {

    private final VoteService voteService;

    //투표하기
    @PostMapping("/api/votes")
    public ResponseEntity<VoteDetailRes> vote(@RequestHeader("X-User-Id") long userId, @RequestBody VoteInfoReq req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(voteService.vote(userId, req));
    }

    //투표자 알아내기
    @PostMapping("/api/votes/open/{voteId}")
    public ResponseEntity<Long> open(@RequestHeader("X-User-Id") long userId, @PathVariable long voteId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(voteService.open(userId, voteId));
    }
}
