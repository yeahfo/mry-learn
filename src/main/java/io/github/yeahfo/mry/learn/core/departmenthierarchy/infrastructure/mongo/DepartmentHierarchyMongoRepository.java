package io.github.yeahfo.mry.learn.core.departmenthierarchy.infrastructure.mongo;

import io.github.yeahfo.mry.learn.core.departmenthierarchy.domain.DepartmentHierarchy;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface DepartmentHierarchyMongoRepository extends MongoRepository< DepartmentHierarchy, String > {
    Optional< DepartmentHierarchy > findByTenantId( String tenantId );
}
