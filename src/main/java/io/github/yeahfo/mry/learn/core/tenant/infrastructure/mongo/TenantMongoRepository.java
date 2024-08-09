package io.github.yeahfo.mry.learn.core.tenant.infrastructure.mongo;

import io.github.yeahfo.mry.learn.core.tenant.domain.Tenant;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface TenantMongoRepository extends MongoRepository< Tenant, String > {
    boolean existsBySubdomainPrefix( String subdomainPrefix );
}
