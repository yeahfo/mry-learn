package io.github.yeahfo.mry.learn.core.member.application;

import com.github.javafaker.Faker;
import io.github.yeahfo.mry.learn.core.member.domain.Member;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Locale;

@SpringBootTest
class MemberServiceTest {
    @Autowired
    private MemberService memberService;
    @Test
    void create( ) {
        Member member = memberService.create( Faker.instance( Locale.CHINA ).name( ).fullName( ) );
        System.err.println( member.id( ) );
        System.err.println( member.name( ) );
    }
}