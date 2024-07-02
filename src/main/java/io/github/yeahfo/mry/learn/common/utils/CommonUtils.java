package io.github.yeahfo.mry.learn.common.utils;

import static org.apache.commons.lang3.StringUtils.isBlank;

public class CommonUtils {
    public static String requireNonBlank( String str, String message ) {
        if ( isBlank( str ) ) {
            throw new IllegalArgumentException( message );
        }
        return str;
    }
}
