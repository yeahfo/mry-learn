package io.github.yeahfo.mry.learn.common.password;


import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

public interface PasswordEncoderFactories {

    static PasswordEncoder createDelegatingPasswordEncoder( ) {
        return new BCryptPasswordEncoder( );
    }
}
