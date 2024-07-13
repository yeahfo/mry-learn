package io.github.yeahfo.mry.learn.core.member.domain;

import java.util.Optional;

public interface MemberRepository {
    Member save( Member member );
    Optional<Member> findById( Long id );
}
