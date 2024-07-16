package io.github.yeahfo.mry.learn.core.verification.infrastructure.mongo;

import io.github.yeahfo.mry.learn.core.verification.domain.VerificationCode;
import io.github.yeahfo.mry.learn.core.verification.domain.VerificationCodeType;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface VerificationCodeMongoRepository extends MongoRepository< VerificationCode, String > {
    Optional< VerificationCode > findByMobileEmailAndCodeAndType( String mobileEmail, String code, VerificationCodeType type );
}
