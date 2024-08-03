package io.github.yeahfo.mry.learn.core.department.infrastructure;

import io.github.yeahfo.mry.learn.core.department.domain.Department;
import io.github.yeahfo.mry.learn.core.department.domain.DepartmentRepository;
import io.github.yeahfo.mry.learn.core.department.domain.TenantCachedDepartment;
import io.github.yeahfo.mry.learn.core.department.infrastructure.mongo.DepartmentMongoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.*;

import static io.github.yeahfo.mry.learn.core.common.utils.CommonUtils.requireNonBlank;
import static io.github.yeahfo.mry.learn.core.common.utils.MryConstants.DEPARTMENT_COLLECTION;
import static io.github.yeahfo.mry.learn.core.common.utils.MryConstants.TENANT_DEPARTMENTS_CACHE;
import static org.apache.commons.collections4.CollectionUtils.isEmpty;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

@Repository
@RequiredArgsConstructor
public class DepartmentRepositoryImpl implements DepartmentRepository {
    private final MongoTemplate mongoTemplate;
    private final DepartmentMongoRepository mongoRepository;


    @Override
    public Department save( Department department ) {
        return mongoRepository.save( department );
    }

    @Override
    public Optional< Department > findById( String id ) {
        return mongoRepository.findById( id );
    }

    @Override
    public boolean notAllDepartmentsExist( List< String > departmentIds, String tenantId ) {
        Objects.requireNonNull( tenantId, "Tenant id must not be null" );
        if ( isEmpty( departmentIds ) ) {
            return false;
        }
        return !new HashSet<>( mongoRepository.findAllByTenantId( tenantId ).stream( )
                .map( Department::identifier ).toList( ) )
                .containsAll( departmentIds );
    }

    @Override
    public List< Department > tenantAllDepartments( String tenantId ) {
        return mongoRepository.findAllByTenantId( tenantId );
    }

    //必须返回ArrayList而非List，否则缓存中由于没有ArrayList类型信息而失败
    @Cacheable( value = TENANT_DEPARTMENTS_CACHE, key = "#tenantId" )
    @Override
    public ArrayList< TenantCachedDepartment > findAllDepartmentsByTenantId( String tenantId ) {
        requireNonBlank( tenantId, "Tenant ID must not be blank." );

        Query query = query( where( "tenantId" ).is( tenantId ) );
        query.fields( ).include( "name", "managers", "customId" );
        return new ArrayList<>( mongoTemplate.find( query, TenantCachedDepartment.class, DEPARTMENT_COLLECTION ) );
    }
}
