package io.github.yeahfo.mry.learn.core.departmenthierarchy.domain;

import io.github.yeahfo.mry.learn.core.common.domain.AggregateRoot;
import io.github.yeahfo.mry.learn.core.common.domain.User;
import io.github.yeahfo.mry.learn.core.common.domain.idnode.IdTree;
import io.github.yeahfo.mry.learn.core.common.domain.idnode.IdTreeHierarchy;
import io.github.yeahfo.mry.learn.core.common.domain.idnode.exception.IdNodeLevelOverflowException;
import io.github.yeahfo.mry.learn.core.common.exception.MryException;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

import static io.github.yeahfo.mry.learn.core.common.exception.ErrorCode.DEPARTMENT_HIERARCHY_TOO_DEEP;
import static io.github.yeahfo.mry.learn.core.common.utils.MryConstants.MAX_GROUP_HIERARCHY_LEVEL;
import static io.github.yeahfo.mry.learn.core.common.utils.SnowflakeIdGenerator.newSnowflakeIdAsString;

public class DepartmentHierarchy extends AggregateRoot {
    private IdTree idTree;
    private IdTreeHierarchy hierarchy;

    public DepartmentHierarchy( User user ) {
        super( newDepartmentHierarchyId( ), user );
        this.idTree = new IdTree( new ArrayList<>( 0 ) );
        this.buildHierarchy( );
        addOpsLog( "新建", user );
    }

    private static String newDepartmentHierarchyId( ) {
        return newSnowflakeIdAsString( );
    }

    public Set< String > directChildDepartmentIdsUnder( String parentDepartmentId ) {
        return this.hierarchy.directChildIdsUnder( parentDepartmentId );
    }


    private void buildHierarchy( ) {
        try {
            this.hierarchy = this.idTree.buildHierarchy( MAX_GROUP_HIERARCHY_LEVEL );//深度与group保持相同，因为可能要同步到group
        } catch ( IdNodeLevelOverflowException ex ) {
            throw new MryException( DEPARTMENT_HIERARCHY_TOO_DEEP, "部门层级最多不能超过5层。", "tenantId", this.tenantId( ) );
        }
    }

    public Map< String, String > departmentFullNames( Map< String, String > departmentNames ) {
        return this.hierarchy.fullNames(departmentNames);
    }
}
