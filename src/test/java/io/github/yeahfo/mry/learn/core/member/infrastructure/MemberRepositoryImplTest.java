package io.github.yeahfo.mry.learn.core.member.infrastructure;

import io.github.yeahfo.mry.learn.core.member.domain.Member;
import io.github.yeahfo.mry.learn.core.member.domain.MemberRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class MemberRepositoryImplTest {
    @Autowired
    MemberRepository repository;

    @Test
    void save( ) {
        Member member = repository.save( Member.of( ) );
        System.err.println( member );
    }

    @Test
    void findById( ) {
        Optional< Member > byId = repository.findById( 599923299618655232L );
        byId.ifPresent( member -> {
            System.err.println( member.id( ) );
            System.err.println( member.name( ) );
        } );

    }
}