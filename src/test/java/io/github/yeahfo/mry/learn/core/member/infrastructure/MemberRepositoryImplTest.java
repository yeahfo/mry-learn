package io.github.yeahfo.mry.learn.core.member.infrastructure;

import io.github.yeahfo.mry.learn.core.member.domain.Member;
import io.github.yeahfo.mry.learn.core.member.domain.MemberFactory;
import io.github.yeahfo.mry.learn.core.member.domain.MemberRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

@SpringBootTest
class MemberRepositoryImplTest {
    @Autowired
    MemberRepository repository;

    @Autowired
    MemberFactory memberFactory;
    @Test
    void save( ) {
    }

    @Test
    void findById( ) {
        Optional< Member > byId = repository.findById( 600328220981701632L );
        byId.ifPresent( member -> {
            System.err.println( member.id( ) );
            System.err.println( member.name( ) );
        } );

    }
}