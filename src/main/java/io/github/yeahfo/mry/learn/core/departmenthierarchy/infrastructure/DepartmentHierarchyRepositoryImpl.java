package io.github.yeahfo.mry.learn.core.departmenthierarchy.infrastructure;

import io.github.yeahfo.mry.learn.core.common.exception.MryException;
import io.github.yeahfo.mry.learn.core.departmenthierarchy.domain.DepartmentHierarchy;
import io.github.yeahfo.mry.learn.core.departmenthierarchy.domain.DepartmentHierarchyRepository;
import io.github.yeahfo.mry.learn.core.departmenthierarchy.infrastructure.mongo.DepartmentHierarchyMongoRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import static io.github.yeahfo.mry.learn.core.common.exception.ErrorCode.DEPARTMENT_HIERARCHY_NOT_FOUND;
import static io.github.yeahfo.mry.learn.core.common.utils.CommonUtils.requireNonBlank;
import static io.github.yeahfo.mry.learn.core.common.utils.MapUtils.mapOf;
import static io.github.yeahfo.mry.learn.core.common.utils.MryConstants.DEPARTMENT_HIERARCHY_CACHE;

@Repository
public class DepartmentHierarchyRepositoryImpl implements DepartmentHierarchyRepository {
    private final DepartmentHierarchyMongoRepository mongoRepository;

    public DepartmentHierarchyRepositoryImpl( DepartmentHierarchyMongoRepository mongoRepository ) {
        this.mongoRepository = mongoRepository;
    }

    @Override
    public DepartmentHierarchy save( DepartmentHierarchy departmentHierarchy ) {
        return mongoRepository.save( departmentHierarchy );
    }

    @Override
    public Optional< DepartmentHierarchy > findById( String id ) {
        return mongoRepository.findById( id );
    }


    @Override
    @Cacheable( value = DEPARTMENT_HIERARCHY_CACHE, key = "#tenantId" )
    public DepartmentHierarchy findByTenantId( String tenantId ) {
        requireNonBlank( tenantId, "Tenant ID must not be blank." );
        return mongoRepository.findByTenantId( tenantId )
                .orElseThrow( ( ) -> new MryException( DEPARTMENT_HIERARCHY_NOT_FOUND, "未找到部门层级。", mapOf( "tenantId", tenantId ) ) );
    }
}
