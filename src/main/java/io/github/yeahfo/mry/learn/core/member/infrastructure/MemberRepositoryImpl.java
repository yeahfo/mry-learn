package io.github.yeahfo.mry.learn.core.member.infrastructure;

import io.github.yeahfo.mry.learn.core.member.domain.Member;
import io.github.yeahfo.mry.learn.core.member.domain.MemberRepository;
import io.github.yeahfo.mry.learn.core.member.infrastructure.mongo.MemberMongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class MemberRepositoryImpl implements MemberRepository {
    private final MemberMongoRepository mongoRepository;

    public MemberRepositoryImpl( MemberMongoRepository mongoRepository ) {
        this.mongoRepository = mongoRepository;
    }

    @Override
    public Member save( Member member ) {
        return mongoRepository.save( member );
    }

    @Override
    public Optional< Member > findById( Long id ) {
        return mongoRepository.findById( id );
    }
}
