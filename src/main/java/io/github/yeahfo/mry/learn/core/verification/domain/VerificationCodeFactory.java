package io.github.yeahfo.mry.learn.core.verification.domain;

import io.github.yeahfo.mry.learn.core.common.domain.User;
import io.github.yeahfo.mry.learn.core.common.exception.MryException;
import io.github.yeahfo.mry.learn.core.tenant.domain.PackagesStatus;
import io.github.yeahfo.mry.learn.core.tenant.domain.TenantRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static io.github.yeahfo.mry.learn.core.common.exception.ErrorCode.TOO_MANY_VERIFICATION_CODE_FOR_TODAY;
import static io.github.yeahfo.mry.learn.core.common.exception.ErrorCode.VERIFICATION_CODE_ALREADY_SENT;
import static io.github.yeahfo.mry.learn.core.common.utils.CommonUtils.isMobileNumber;
import static io.github.yeahfo.mry.learn.core.common.utils.CommonUtils.maskMobileOrEmail;
import static org.apache.commons.lang3.StringUtils.isNotBlank;


@Slf4j
@Component
@RequiredArgsConstructor
public class VerificationCodeFactory {
    private final TenantRepository tenantRepository;
    private final VerificationCodeRepository repository;

    public Optional< VerificationCode > create( String mobileOrEmail, VerificationCodeType type, String tenantId, User user ) {
        try {
            if ( repository.existsWithinOneMinutes( mobileOrEmail, type ) ) {
                throw new MryException( VERIFICATION_CODE_ALREADY_SENT, "1分钟内只能获取一次验证码。",
                        "mobileOrEmail", maskMobileOrEmail( mobileOrEmail ) );
            }

            if ( repository.totalCodeCountOfTodayFor( mobileOrEmail ) > 20 ) {
                throw new MryException( TOO_MANY_VERIFICATION_CODE_FOR_TODAY, "验证码获取次数超过当天限制。",
                        "mobileOrEmail", maskMobileOrEmail( mobileOrEmail ) );
            }

            if ( isNotBlank( tenantId ) && isMobileNumber( mobileOrEmail ) ) {
                PackagesStatus packagesStatus = tenantRepository.packagesStatusOf( tenantId );
                if ( packagesStatus.isMaxSmsCountReached( ) ) {
                    log.warn( "Failed to create verification code for [{}] as SMS count reached max amount for current month for tenant[{}].",
                            maskMobileOrEmail( mobileOrEmail ), tenantId );
                    return Optional.empty( );
                }
            }

            return Optional.of( new VerificationCode( mobileOrEmail, type, tenantId, user ) );
        } catch ( MryException mryException ) {
            log.warn( "Error while create verification code: {}.", mryException.getMessage( ) );
            return Optional.empty( );
        }
    }
}
