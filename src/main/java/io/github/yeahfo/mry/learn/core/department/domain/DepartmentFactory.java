package io.github.yeahfo.mry.learn.core.department.domain;

import io.eventuate.tram.events.aggregates.ResultWithDomainEvents;
import io.github.yeahfo.mry.learn.core.common.domain.User;
import io.github.yeahfo.mry.learn.core.common.exception.MryException;
import io.github.yeahfo.mry.learn.core.department.domain.event.DepartmentCreatedEvent;
import io.github.yeahfo.mry.learn.core.department.domain.event.DepartmentDomainEvent;
import io.github.yeahfo.mry.learn.core.departmenthierarchy.domain.DepartmentHierarchy;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;

import static com.google.common.collect.ImmutableSet.toImmutableSet;
import static io.github.yeahfo.mry.learn.core.common.exception.ErrorCode.DEPARTMENT_WITH_NAME_ALREADY_EXISTS;

@Component
public class DepartmentFactory {
    private final DepartmentRepository departmentRepository;

    public DepartmentFactory( DepartmentRepository departmentRepository ) {
        this.departmentRepository = departmentRepository;
    }

    public ResultWithDomainEvents< Department, DepartmentDomainEvent > create( String name,
                                                                               String tenantId,
                                                                               String parentDepartmentId,
                                                                               DepartmentHierarchy departmentHierarchy,
                                                                               User user ) {
        return this.create( name, tenantId, parentDepartmentId, departmentHierarchy, null, user );
    }

    public ResultWithDomainEvents< Department, DepartmentDomainEvent > create( String name,
                                                                               String tenantId,
                                                                               String parentDepartmentId,
                                                                               DepartmentHierarchy departmentHierarchy,
                                                                               String customId,
                                                                               User user ) {
        checkNameDuplication( name, tenantId, parentDepartmentId, departmentHierarchy );
        return new ResultWithDomainEvents<>( new Department( name, customId, user ), new DepartmentCreatedEvent( user ) );
    }

    private void checkNameDuplication( String name,
                                       String tenantId,
                                       String parentDepartmentId,
                                       DepartmentHierarchy departmentHierarchy ) {
        Set< String > siblingDepartmentIds = departmentHierarchy.directChildDepartmentIdsUnder( parentDepartmentId );
        Set< String > siblingDepartmentNames = departmentRepository.tenantAllDepartments( tenantId ).stream( )
                .filter( department -> siblingDepartmentIds.contains( department.identifier( ).toString( ) ) )
                .map( Department::name )
                .collect( toImmutableSet( ) );

        if ( siblingDepartmentNames.contains( name ) ) {
            throw new MryException( DEPARTMENT_WITH_NAME_ALREADY_EXISTS, "创建失败，名称已被占用。",
                    Map.of( "name", name, "tenantId", tenantId ) );
        }
    }
}
