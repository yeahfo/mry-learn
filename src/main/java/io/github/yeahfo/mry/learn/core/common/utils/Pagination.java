package io.github.yeahfo.mry.learn.core.common.utils;

import static io.github.yeahfo.mry.learn.core.common.exception.MryException.requestValidationException;

public class Pagination {
   private final int page;
   private final int size;

    private Pagination( int page, int size ) {
        if ( page < 1 ) {
            throw requestValidationException( "detail", "page不能小于1" );
        }

        if ( page > 10000 ) {
            throw requestValidationException( "detail", "page不能大于10000" );
        }

        if ( size < 10 ) {
            throw requestValidationException( "detail", "size不能小于10" );
        }

        if ( size > 500 ) {
            throw requestValidationException( "detail", "size不能大于500" );
        }

        this.page = page;
        this.size = size;
    }

    public static Pagination pagination( int pageIndex, int pageSize ) {
        return new Pagination( pageIndex, pageSize );
    }

    public int page( ) {
        return page;
    }

    public int size( ) {
        return size;
    }

    public int skip( ) {
        return ( this.page - 1 ) * this.size;
    }

    public int limit( ) {
        return this.page;
    }
}
