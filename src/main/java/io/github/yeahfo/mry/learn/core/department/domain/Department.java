package io.github.yeahfo.mry.learn.core.department.domain;

import io.github.yeahfo.mry.learn.core.common.domain.AggregateRoot;
import io.github.yeahfo.mry.learn.core.common.domain.User;
import io.github.yeahfo.mry.learn.core.common.utils.SnowflakeIdGenerator;

import java.util.List;

public class Department extends AggregateRoot {
    private String name;
    private List< String > managers;
    private String customId;

    public Department( String name, String customId, User user ) {
        super( SnowflakeIdGenerator.newSnowflakeId( ), user );
        this.name = name;
        this.managers = List.of( );
        this.customId = customId;
        addOpsLog( "新建", user );
    }

    public String name( ) {
        return name;
    }
}
