package io.github.yeahfo.mry.learn.common.domain;

import java.time.Instant;

public record OpsLog( Instant operatedAt,
                      String operatedBy,
                      String operator,
                      String note ) {
}
