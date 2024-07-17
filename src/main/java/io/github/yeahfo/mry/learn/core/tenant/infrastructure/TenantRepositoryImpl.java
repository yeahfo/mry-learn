package io.github.yeahfo.mry.learn.core.tenant.infrastructure;

import io.github.yeahfo.mry.learn.core.common.exception.MryException;
import io.github.yeahfo.mry.learn.core.tenant.domain.PackagesStatus;
import io.github.yeahfo.mry.learn.core.tenant.domain.Tenant;
import io.github.yeahfo.mry.learn.core.tenant.domain.TenantRepository;
import io.github.yeahfo.mry.learn.core.tenant.infrastructure.mongo.TenantMongoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;

import static io.github.yeahfo.mry.learn.core.common.exception.ErrorCode.TENANT_NOT_FOUND;
import static io.github.yeahfo.mry.learn.core.common.utils.CommonUtils.requireNonBlank;
import static io.github.yeahfo.mry.learn.core.common.utils.MryConstants.TENANT_COLLECTION;
import static org.springframework.data.mongodb.core.query.Criteria.where;

@Repository
@RequiredArgsConstructor
public class TenantRepositoryImpl implements TenantRepository {
    private final MongoTemplate mongoTemplate;
    private final TenantMongoRepository mongoRepository;


    @Override
    public Tenant save( Tenant tenant ) {
        return mongoRepository.save( tenant );
    }

    @Override
    public Optional< Tenant > findById( String id ) {
        return mongoRepository.findById( id );
    }

    @Override
    public boolean exists( String tenantId ) {
        return mongoRepository.existsById( tenantId );
    }

    @Override
    public PackagesStatus packagesStatusOf( String tenantId ) {
        requireNonBlank( tenantId, "Tenant ID must not be blank." );

        Query query = Query.query( where( "_id" ).is( tenantId ) );
        query.fields( ).include( "packages" ).include( "resourceUsage" );
        PackagesStatus packagesStatus = mongoTemplate.findOne( query, PackagesStatus.class, TENANT_COLLECTION );

        if ( packagesStatus == null ) {
            throw new MryException( TENANT_NOT_FOUND, "没有找到租户。", Map.of( "id", tenantId ) );
        }

        return packagesStatus;
    }
}
