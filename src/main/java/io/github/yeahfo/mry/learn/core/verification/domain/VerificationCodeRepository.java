package io.github.yeahfo.mry.learn.core.verification.domain;

import io.github.yeahfo.mry.learn.core.common.domain.AggregateRootRepository;

import java.util.Optional;

public interface VerificationCodeRepository extends AggregateRootRepository< VerificationCode, String > {
    Optional< VerificationCode > findByMobileEmailAndCodeAndType( String mobileEmail, String code, VerificationCodeType type );
}
