package io.github.yeahfo.mry.learn.core.department.infrastructure;

import io.github.yeahfo.mry.learn.core.department.domain.Department;
import io.github.yeahfo.mry.learn.core.department.domain.DepartmentRepository;
import io.github.yeahfo.mry.learn.core.department.infrastructure.mongo.DepartmentMongoRepository;
import org.springframework.stereotype.Repository;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.apache.commons.collections4.CollectionUtils.isEmpty;

@Repository
public class DepartmentRepositoryImpl implements DepartmentRepository {
    private final DepartmentMongoRepository mongoRepository;

    public DepartmentRepositoryImpl( DepartmentMongoRepository mongoRepository ) {
        this.mongoRepository = mongoRepository;
    }

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
}
