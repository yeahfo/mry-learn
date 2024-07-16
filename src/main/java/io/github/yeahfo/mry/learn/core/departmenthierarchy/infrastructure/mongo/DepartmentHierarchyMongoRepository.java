package io.github.yeahfo.mry.learn.core.departmenthierarchy.infrastructure.mongo;

import io.github.yeahfo.mry.learn.core.departmenthierarchy.domain.DepartmentHierarchy;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface DepartmentHierarchyMongoRepository extends MongoRepository< DepartmentHierarchy, String> {
}
