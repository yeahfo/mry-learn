package io.github.yeahfo.mry.learn.core.common.domain;


import io.github.yeahfo.mry.learn.core.common.utils.Identified;
import lombok.Getter;
import org.springframework.data.annotation.Version;

import java.time.Instant;
import java.util.LinkedList;
import java.util.List;

import static java.time.Instant.now;
import static java.util.Objects.requireNonNull;
import static lombok.AccessLevel.PRIVATE;

public abstract class AggregateRoot implements Identified {
    protected static final int MAX_OPS_LOG_SIZE = 20;
    protected String id;//通过Snowflake算法生成
    protected String tenantId;//在多租户下，所有聚合根都需要一个tenantId来对应某个租户
    protected Instant createdAt;//创建时间
    protected String createdBy;//创建人的MemberId
    protected String creator;//创建人姓名
    protected Instant updatedAt;//更新时间
    protected String updatedBy;//更新人MemberId
    protected String updater;//更新人姓名
    protected LinkedList< OpsLog > opsLogs;//操作日志

    @Version
    @Getter( PRIVATE )
    protected Long _version;//版本号，实现乐观锁

    protected AggregateRoot( String id, User user ) {
        requireNonNull( id, "ID must not be blank." );
        requireNonNull( user, "User must not be null." );
        requireNonNull( user.tenantId( ), "Tenant ID must not be blank." );

        this.id = id;
        this.tenantId = user.tenantId( );
        this.createdAt = now( );
        this.createdBy = user.memberId( );
        this.creator = user.name( );
    }

    protected AggregateRoot( String id, String tenantId, User user ) {
        requireNonNull( id, "AR ID must not be blank." );
        requireNonNull( tenantId, "Tenant ID must not be blank." );
        requireNonNull( user, "User must not be null." );

        this.id = id;
        this.tenantId = tenantId;
        this.createdAt = now( );
        this.createdBy = user.memberId( );
        this.creator = user.name( );
    }

    protected void addOpsLog( String note, User user ) {
        if ( user.isLoggedIn( ) ) {
            OpsLog log = OpsLog.builder( ).note( note ).operatedAt( now( ) ).operatedBy( user.memberId( ) ).operator( user.name( ) ).build( );
            List< OpsLog > opsLogs = allOpsLogs( );

            opsLogs.add( log );
            if ( opsLogs.size( ) > MAX_OPS_LOG_SIZE ) {//最多保留最近100条
                this.opsLogs.remove( );
            }

            this.updatedAt = now( );
            this.updatedBy = user.memberId( );
            this.updater = user.name( );
        }
    }

    protected List< OpsLog > allOpsLogs( ) {
        if ( opsLogs == null ) {
            this.opsLogs = new LinkedList<>( );
        }

        return opsLogs;
    }


    @Override
    public String identifier( ) {
        return id;
    }

    public String id( ) {
        return id;
    }

    public String tenantId( ) {
        return tenantId;
    }

    public Instant createdAt( ) {
        return createdAt;
    }

    public String createdBy( ) {
        return createdBy;
    }

    public String creator( ) {
        return creator;
    }

    public Instant updatedAt( ) {
        return updatedAt;
    }

    public String updatedBy( ) {
        return updatedBy;
    }

    public String updater( ) {
        return updater;
    }

    public LinkedList< OpsLog > opsLogs( ) {
        return opsLogs;
    }
}
