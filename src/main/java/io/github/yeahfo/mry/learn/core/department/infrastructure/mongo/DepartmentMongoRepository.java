package io.github.yeahfo.mry.learn.core.department.infrastructure.mongo;

import io.github.yeahfo.mry.learn.core.department.domain.Department;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

import static io.github.yeahfo.mry.learn.core.common.utils.MryConstants.TENANT_DEPARTMENTS_CACHE;

public interface DepartmentMongoRepository extends MongoRepository< Department, String > {

    @Cacheable( value = TENANT_DEPARTMENTS_CACHE, key = "#tenantId" )
    List< Department > findAllByTenantId( String tenantId );
}
