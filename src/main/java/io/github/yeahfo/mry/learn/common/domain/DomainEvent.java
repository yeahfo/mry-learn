package io.github.yeahfo.mry.learn.common.domain;

import java.time.Instant;

public abstract class DomainEvent {
    private String id;//事件ID，不能为空
    private String arTenantId;//事件对应的租户ID，不能为空
    private String arId;//事件对应的聚合根ID，不能为空
    private DomainEventType type;//事件类型
    private DomainEventStatus status;//状态
    private int publishedCount;//已经发布的次数，无论成功与否
    private int consumedCount;//已经被消费的次数，无论成功与否
    private String raisedBy;//引发该事件的memberId
    private Instant raisedAt;//事件产生时间
}
