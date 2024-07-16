package io.github.yeahfo.mry.learn.core.common.exception;

import lombok.AllArgsConstructor;
import lombok.Value;

import java.time.Instant;
import java.util.Map;

import static java.time.Instant.now;
import static lombok.AccessLevel.PRIVATE;


@Value
@AllArgsConstructor( access = PRIVATE )
public class Error {
    ErrorCode code;
    String message;
    String userMessage;
    int status;
    String path;
    Instant timestamp;
    String traceId;
    Map< String, Object > data;

    public Error( MryException ex, String path, String traceId ) {
        ErrorCode errorCode = ex.getCode( );
        this.code = errorCode;
        this.message = ex.getMessage( );
        this.userMessage = ex.getUserMessage( );
        this.status = errorCode.getStatus( );
        this.path = path;
        this.timestamp = now( );
        this.traceId = traceId;
        this.data = ex.getData( );
    }

    public Error( ErrorCode code, int status, String message, String path, String traceId, Map< String, Object > data ) {
        this.code = code;
        this.message = message;
        this.userMessage = message;
        this.status = status;
        this.path = path;
        this.timestamp = now( );
        this.traceId = traceId;
        this.data = data;
    }

    public ErrorRepresentation representation( ) {
        return ErrorRepresentation.builder( ).error( this ).build( );
    }
}
