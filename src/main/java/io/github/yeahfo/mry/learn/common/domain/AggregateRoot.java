package io.github.yeahfo.mry.learn.common.domain;

import java.time.Instant;
import java.util.LinkedList;
import java.util.List;

public abstract class AggregateRoot implements Identified {
    private String id;//通过Snowflake算法生成
    private String tenantId;//在多租户下，所有聚合根都需要一个tenantId来对应某个租户
    private Instant createdAt;//创建时间
    private String createdBy;//创建人的MemberId
    private String creator;//创建人姓名
    private Instant updatedAt;//更新时间
    private String updatedBy;//更新人MemberId
    private String updater;//更新人姓名
    private List< DomainEvent > events;//领域事件列表，用于临时存放完成某个业务流程中所发出的事件，会被BaseRepository保存到事件表中
    private LinkedList< OpsLog > opsLogs;//操作日志

    @Override
    public String getIdentifier( ) {
        return id;
    }
}
