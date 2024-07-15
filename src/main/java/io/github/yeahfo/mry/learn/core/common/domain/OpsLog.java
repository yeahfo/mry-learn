package io.github.yeahfo.mry.learn.core.common.domain;

import lombok.Builder;

import java.time.Instant;

@Builder
public record OpsLog( Instant operatedAt, // 操作时间
                      Long operatedBy,//操作人id
                      String operator,//操作人姓名
                      String note ) {
}
