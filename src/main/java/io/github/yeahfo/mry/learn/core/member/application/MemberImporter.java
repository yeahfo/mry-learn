package io.github.yeahfo.mry.learn.core.member.application;

import io.github.yeahfo.mry.learn.core.common.domain.User;
import io.github.yeahfo.mry.learn.core.common.exception.MryException;
import io.github.yeahfo.mry.learn.core.member.application.command.MemberImportRecord;
import io.github.yeahfo.mry.learn.core.member.application.representation.MemberImportRepresentation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.List;

import static com.google.common.collect.ImmutableList.toImmutableList;

@Slf4j
@Component
@RequiredArgsConstructor
public class MemberImporter {
    private final MemberImportSaver memberImportSaver;
    private final MemberImportParser memberImportParser;

    public MemberImportRepresentation importMembers( InputStream inputStream, int limit, User user ) {
        List< MemberImportRecord > records = memberImportParser.parse( inputStream, user.tenantId( ), limit );

        records.forEach( record -> {
            try {
                if ( !record.hasError( ) ) {
                    memberImportSaver.save( record, user );
                }
            } catch ( MryException mryException ) {
                record.addError( mryException.getUserMessage( ) );
            }
        } );

        List< MemberImportRecord > errorRecords = records.stream( ).filter( MemberImportRecord::hasError ).collect( toImmutableList( ) );

        return MemberImportRepresentation.builder( )
                .readCount( records.size( ) )
                .importedCount( records.size( ) - errorRecords.size( ) )
                .errorRecords( errorRecords )
                .build( );
    }
}
