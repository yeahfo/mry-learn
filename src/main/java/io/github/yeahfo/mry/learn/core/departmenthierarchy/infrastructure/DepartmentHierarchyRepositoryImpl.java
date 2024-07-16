package io.github.yeahfo.mry.learn.core.departmenthierarchy.infrastructure;

import io.github.yeahfo.mry.learn.core.departmenthierarchy.domain.DepartmentHierarchy;
import io.github.yeahfo.mry.learn.core.departmenthierarchy.domain.DepartmentHierarchyRepository;
import io.github.yeahfo.mry.learn.core.departmenthierarchy.infrastructure.mongo.DepartmentHierarchyMongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

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
}
