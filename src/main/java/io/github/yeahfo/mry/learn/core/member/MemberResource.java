package io.github.yeahfo.mry.learn.core.member;

import io.github.yeahfo.mry.learn.core.member.application.MemberService;
import io.github.yeahfo.mry.learn.core.member.domain.Member;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MemberResource {
    private final MemberService memberService;

    public MemberResource( MemberService memberService ) {
        this.memberService = memberService;
    }

}