package io.github.yeahfo.mry.learn.core.verification.infrastructure;

import io.github.yeahfo.mry.learn.core.verification.domain.VerificationCode;
import io.github.yeahfo.mry.learn.core.verification.domain.VerificationCodeRepository;
import io.github.yeahfo.mry.learn.core.verification.domain.VerificationCodeType;
import io.github.yeahfo.mry.learn.core.verification.infrastructure.mongo.VerificationCodeMongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class VerificationCodeRepositoryImpl implements VerificationCodeRepository {
    private final VerificationCodeMongoRepository mongoRepository;

    public VerificationCodeRepositoryImpl( VerificationCodeMongoRepository mongoRepository ) {
        this.mongoRepository = mongoRepository;
    }

    @Override
    public VerificationCode save( VerificationCode verificationCode ) {
        return mongoRepository.save( verificationCode );
    }

    @Override
    public Optional< VerificationCode > findById( String id ) {
        return mongoRepository.findById( id );
    }

    @Override
    public Optional< VerificationCode > findByMobileEmailAndCodeAndType( String mobileEmail, String code, VerificationCodeType type ) {
        return mongoRepository.findByMobileEmailAndCodeAndType( mobileEmail, code, type );
    }
}
