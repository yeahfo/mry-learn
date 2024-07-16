package io.github.yeahfo.mry.learn.core.tenant.infrastructure;

import io.github.yeahfo.mry.learn.core.tenant.domain.Tenant;
import io.github.yeahfo.mry.learn.core.tenant.domain.TenantRepository;
import io.github.yeahfo.mry.learn.core.tenant.infrastructure.mongo.TenantMongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class TenantRepositoryImpl implements TenantRepository {
    private final TenantMongoRepository mongoRepository;

    public TenantRepositoryImpl( TenantMongoRepository mongoRepository ) {
        this.mongoRepository = mongoRepository;
    }

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
}
