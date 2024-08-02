package io.github.yeahfo.mry.learn.core.member.application.representation;

import io.github.yeahfo.mry.learn.core.member.application.command.MemberImportRecord;
import lombok.Builder;

import java.util.List;

@Builder
public record MemberImportRepresentation( int readCount,
                                          int importedCount,
                                          List< MemberImportRecord > errorRecords ) {
}
