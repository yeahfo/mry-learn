package io.github.yeahfo.mry.learn.core.member.application;

import io.eventuate.tram.events.aggregates.ResultWithDomainEvents;
import io.github.yeahfo.mry.learn.common.password.PasswordEncoderFactories;
import io.github.yeahfo.mry.learn.core.common.domain.User;
import io.github.yeahfo.mry.learn.core.member.application.command.MemberImportRecord;
import io.github.yeahfo.mry.learn.core.member.domain.Member;
import io.github.yeahfo.mry.learn.core.member.domain.MemberFactory;
import io.github.yeahfo.mry.learn.core.member.domain.MemberRepository;
import io.github.yeahfo.mry.learn.core.member.domain.event.MemberDomainEvent;
import io.github.yeahfo.mry.learn.core.member.domain.event.MemberDomainEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class MemberImportSaver {
    private static final PasswordEncoder passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder( );
    private final MemberFactory memberFactory;
    private final MemberRepository memberRepository;
    private final MemberDomainEventPublisher domainEventPublisher;

    @Transactional
    public void save( MemberImportRecord record, User user ) {
        ResultWithDomainEvents< Member, MemberDomainEvent > resultWithDomainEvents = memberFactory.create( record.getName( ),
                List.of( ),
                record.getMobile( ),
                record.getEmail( ),
                passwordEncoder.encode( record.getPassword( ) ),
                record.getCustomId( ),
                user );
        Member member = memberRepository.save( resultWithDomainEvents.result );
        domainEventPublisher.publish( member, resultWithDomainEvents.events );
    }
}
