package io.github.yeahfo.mry.learn.core.common.domain;


import jakarta.validation.constraints.Size;
import org.apache.commons.lang3.StringUtils;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

import static java.util.stream.Collectors.joining;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

public record Address( @Size( max = 20 )
                       String province,
                       @Size( max = 20 )
                       String city,
                       @Size( max = 20 )
                       String district,

                       @Size( max = 100 )
                       String address ) {
    private static final String ADDRESS_JOINER = "/";

    public static String joinAddress( String... addressPart ) {
        return String.join( ADDRESS_JOINER, addressPart );
    }

    public boolean isFilled( ) {
        return isNotBlank( province ) ||
                isNotBlank( city ) ||
                isNotBlank( district ) ||
                isNotBlank( address );
    }

    public Set< String > indexedValues( ) {
        if ( isBlank( province ) ) {
            return null;
        }

        Set< String > results = new HashSet<>( );
        results.add( province );
        if ( isNotBlank( city ) ) {
            results.add( joinAddress( province, city ) );
        }

        if ( isNotBlank( city ) && isNotBlank( district ) ) {
            results.add( joinAddress( province, city, district ) );
        }
        return Set.copyOf( results );
    }

    public String toText( ) {
        return Stream.of( province,
                        city,
                        district,
                        address )
                .filter( StringUtils::isNotBlank ).collect( joining( ) );
    }
}
