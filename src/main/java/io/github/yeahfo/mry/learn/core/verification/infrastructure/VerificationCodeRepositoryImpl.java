package io.github.yeahfo.mry.learn.core.verification.infrastructure;

import io.github.yeahfo.mry.learn.core.verification.domain.VerificationCode;
import io.github.yeahfo.mry.learn.core.verification.domain.VerificationCodeRepository;
import io.github.yeahfo.mry.learn.core.verification.domain.VerificationCodeType;
import io.github.yeahfo.mry.learn.core.verification.infrastructure.mongo.VerificationCodeMongoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Optional;

import static io.github.yeahfo.mry.learn.core.common.utils.CommonUtils.requireNonBlank;
import static java.time.Instant.now;
import static java.time.ZoneId.systemDefault;
import static java.time.temporal.ChronoUnit.MINUTES;
import static java.util.Objects.requireNonNull;
import static org.springframework.data.mongodb.core.query.Criteria.where;


@Repository
@RequiredArgsConstructor
public class VerificationCodeRepositoryImpl implements VerificationCodeRepository {
    private final MongoTemplate mongoTemplate;
    private final VerificationCodeMongoRepository mongoRepository;

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

    @Override
    public boolean existsWithinOneMinutes( String mobileEmail, VerificationCodeType type ) {
        requireNonBlank( mobileEmail, "Mobile or email must not be blank." );
        requireNonNull( type, "Type must not be null." );
        Query query = Query.query( where( "mobileEmail" ).is( mobileEmail )
                .and( "type" ).is( type.name( ) )
                .and( "createdAt" ).gte( now( ).minus( 1, MINUTES ) ) );
        return mongoTemplate.exists( query, VerificationCode.class );
    }

    @Override
    public long totalCodeCountOfTodayFor( String mobileEmail ) {
        requireNonBlank( mobileEmail, "Mobile or email must not be blank." );
        Instant beginOfToday = LocalDate.now( ).atStartOfDay( ).atZone( systemDefault( ) ).toInstant( );
        Query query = Query.query( where( "mobileEmail" ).is( mobileEmail ).and( "createdAt" ).gte( beginOfToday ) );
        return mongoTemplate.count( query, VerificationCode.class );
    }
}
