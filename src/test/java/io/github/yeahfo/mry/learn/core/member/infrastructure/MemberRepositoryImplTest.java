package io.github.yeahfo.mry.learn.core.member.infrastructure;

import com.github.javafaker.Faker;
import io.github.yeahfo.mry.learn.core.member.domain.Member;
import io.github.yeahfo.mry.learn.core.member.domain.MemberRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import java.util.Locale;
import java.util.Optional;

@SpringBootTest
class MemberRepositoryImplTest {
    @Autowired
    MemberRepository repository;

    @Test
    void save( ) {
        Member member = repository.save( Member.create( Faker.instance( Locale.CHINA ).name( ).fullName( ) ).result );
        System.err.println( member.id( ) );
        System.err.println( member.name( ) );
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