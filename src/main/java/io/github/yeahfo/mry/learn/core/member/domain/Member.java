package io.github.yeahfo.mry.learn.core.member.domain;

import com.github.javafaker.Faker;
import io.github.yeahfo.mry.learn.core.common.utils.SnowflakeIdGenerator;

import java.util.Locale;

public class Member {
    private Long id;
    private String name;

    public static Member of( ) {
        Member member = new Member( );
        member.id = SnowflakeIdGenerator.newSnowflakeId( );
        member.name = Faker.instance( Locale.CHINA ).name( ).fullName( );
        return member;
    }

    public Long id( ) {
        return id;
    }

    public String name( ) {
        return name;
    }
}
