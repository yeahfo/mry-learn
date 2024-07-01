package io.github.yeahfo.mry.learn.common.about;

import lombok.Builder;

@Builder
public record QAboutInfo( String buildTime,
                          String deployTime,
                          String gitRevision,
                          String gitBranch,
                          String environment ) {
}
