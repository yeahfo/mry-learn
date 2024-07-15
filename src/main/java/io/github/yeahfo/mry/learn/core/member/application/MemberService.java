package io.github.yeahfo.mry.learn.core.member.application;

import io.github.yeahfo.mry.learn.core.member.domain.*;
import io.github.yeahfo.mry.learn.core.member.domain.event.MemberDomainEventPublisher;
import org.springframework.stereotype.Service;

@Service
public class MemberService {
    private final MemberDomainEventPublisher domainEventPublisher;

    public MemberService( MemberRepository repository, MemberDomainEventPublisher domainEventPublisher ) {
        this.domainEventPublisher = domainEventPublisher;
    }
}
