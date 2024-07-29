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
import static io.github.yeahfo.mry.learn.core.verification.domain.VerificationCodeType.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class VerificationCodeApplicationService {
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
        log.info( "Created verification code id[{}] for register for [{}].", verificationCodeId, maskMobileOrEmail( command.mobileOrEmail( ) ) );
        return verificationCodeId;
    }

    @Transactional
    public String createVerificationCodeForLogin( CreateLoginVerificationCodeCommand command ) {
        String mobileOrEmail = command.mobileOrEmail( );
        rateLimiter.applyFor( "VerificationCode:Login:All", 100 );
        rateLimiter.applyFor( "VerificationCode:Login:" + mobileOrEmail, 1 );
        String verificationCodeId = memberRepository.findByMobileOrEmail( mobileOrEmail )
                .map( member -> createVerificationCode( mobileOrEmail, LOGIN, member.tenantId( ), member.toUser( ) ) )
                .orElseGet( ( ) -> {
                    log.warn( "No user exists for [{}] for login.", maskMobileOrEmail( mobileOrEmail ) );
                    return newVerificationCodeId( );
                } );

        log.info( "Created verification code[{}] for login for [{}].", verificationCodeId, maskMobileOrEmail( command.mobileOrEmail( ) ) );
        return verificationCodeId;
    }

    @Transactional
    public String createVerificationCodeForFindBackPassword( CreateFindBackPasswordVerificationCodeCommand command ) {
        String mobileOrEmail = command.mobileOrEmail( );
        rateLimiter.applyFor( "VerificationCode:FindBackPassword:All", 10 );
        rateLimiter.applyFor( "VerificationCode:FindBackPassword:" + mobileOrEmail, 1 );
        String verificationCodeId = memberRepository.findByMobileOrEmail( mobileOrEmail )
                .map( member -> createVerificationCode( mobileOrEmail, FIND_BACK_PASSWORD, member.tenantId( ), member.toUser( ) ) )
                .orElseGet( ( ) -> {
                    log.warn( "No user exists for [{}] for find back password.", mobileOrEmail );
                    return newVerificationCodeId( );
                } );

        log.info( "Created verification code[{}] for find back password for [{}].",
                verificationCodeId, maskMobileOrEmail( command.mobileOrEmail( ) ) );
        return verificationCodeId;
    }

    @Transactional
    public String createVerificationCodeForChangeMobile( CreateChangeMobileVerificationCodeCommand command, User user ) {
        String mobile = command.mobile( );
        rateLimiter.applyFor( "VerificationCode:ChangeMobile:All", 10 );
        rateLimiter.applyFor( "VerificationCode:ChangeMobile:" + mobile, 1 );

        if ( memberRepository.existsByMobile( mobile ) ) {
            log.warn( "Mobile [{}] already exists for change mobile.", maskMobileOrEmail( mobile ) );
            return newVerificationCodeId( );
        }

        String verificationCodeId = createVerificationCode( mobile, CHANGE_MOBILE, user.tenantId( ), user );
        log.info( "Created verification code[{}] for change mobile for [{}].", verificationCodeId, maskMobileOrEmail( mobile ) );

        return verificationCodeId;
    }

    @Transactional
    public String createVerificationCodeForIdentifyMobile( IdentifyMobileVerificationCodeCommand command, User user ) {
        String mobile = command.mobile( );
        rateLimiter.applyFor( "VerificationCode:IdentifyMobile:All", 20 );
        rateLimiter.applyFor( "VerificationCode:IdentifyMobile:" + mobile, 1 );

        String verificationCodeId = createVerificationCode( mobile, IDENTIFY_MOBILE, user.tenantId( ), user );
        log.info( "Created verification code[{}] for identify mobile for [{}].", verificationCodeId, mobile );
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
