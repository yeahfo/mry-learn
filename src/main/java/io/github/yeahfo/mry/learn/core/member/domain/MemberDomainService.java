package io.github.yeahfo.mry.learn.core.member.domain;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.transaction.annotation.Propagation.REQUIRES_NEW;

@Component
@RequiredArgsConstructor
public class MemberDomainService {
    private final MemberRepository memberRepository;

    @Transactional( propagation = REQUIRES_NEW )//使用REQUIRES_NEW保证即便其他地方有异常，这里也能正常写库
    public void recordMemberFailedLogin( Member member ) {
        member.recordFailedLogin( );
        memberRepository.save( member );
    }
}
