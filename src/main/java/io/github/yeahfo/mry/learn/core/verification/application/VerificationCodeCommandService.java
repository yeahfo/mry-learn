package io.github.yeahfo.mry.learn.core.verification.application;

import io.github.yeahfo.mry.learn.common.ratelimit.RateLimiter;
import io.github.yeahfo.mry.learn.core.common.domain.User;
import io.github.yeahfo.mry.learn.core.member.domain.MemberRepository;
import io.github.yeahfo.mry.learn.core.verification.domain.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static io.github.yeahfo.mry.learn.core.common.domain.User.NOUSER;
import static io.github.yeahfo.mry.learn.core.common.utils.CommonUtils.maskMobileOrEmail;
import static io.github.yeahfo.mry.learn.core.verification.domain.VerificationCode.newVerificationCodeId;
import static io.github.yeahfo.mry.learn.core.verification.domain.VerificationCodeType.REGISTER;

@Slf4j
@Service
@RequiredArgsConstructor
public class VerificationCodeCommandService {
    private final RateLimiter rateLimiter;
    private final VerificationCodeSender sender;
    private final VerificationCodeFactory factory;
    private final MemberRepository memberRepository;
    private final VerificationCodeRepository repository;

    @Transactional
    public String createVerificationCodeForRegister( CreateRegisterVerificationCodeCommand command ) {
        String mobileOrEmail = command.mobileOrEmail( );
        rateLimiter.applyFor( "VerificationCode:Register:All", 20 );
        rateLimiter.applyFor( "VerificationCode:Register:" + mobileOrEmail, 1 );

        if ( memberRepository.existsByMobileOrEmail( mobileOrEmail ) ) {
            log.warn( "[{}] already exists for register.", maskMobileOrEmail( mobileOrEmail ) );
            return newVerificationCodeId( );
        }
        String verificationCodeId = createVerificationCode( mobileOrEmail, REGISTER, null, NOUSER );
        log.info( "Created verification code[{}] for register for [{}].", verificationCodeId, maskMobileOrEmail( command.mobileOrEmail( ) ) );
        return verificationCodeId;
    }

    private String createVerificationCode( String mobileOrEmail, VerificationCodeType type, String tenantId, User user ) {
        Optional< VerificationCode > verificationCodeOptional = factory.create( mobileOrEmail, type, tenantId, user );
        if ( verificationCodeOptional.isPresent( ) ) {
            VerificationCode code = verificationCodeOptional.get( );
            repository.save( code );
            sender.send( code );
            return code.identifier( );
        } else {
            return newVerificationCodeId( );
        }
    }
}
