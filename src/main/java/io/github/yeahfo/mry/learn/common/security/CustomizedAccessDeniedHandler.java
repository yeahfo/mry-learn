package io.github.yeahfo.mry.learn.common.security;

import io.github.yeahfo.mry.learn.common.tracing.TracingService;
import io.github.yeahfo.mry.learn.core.common.utils.CustomizedObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.PrintWriter;

import static io.github.yeahfo.mry.learn.core.common.exception.ErrorCode.ACCESS_DENIED;
import static org.apache.commons.codec.CharEncoding.UTF_8;
import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON_VALUE;

@Component
public class CustomizedAccessDeniedHandler implements AccessDeniedHandler {
    private final TracingService mryTracingService;
    private final CustomizedObjectMapper objectMapper;

    public CustomizedAccessDeniedHandler( TracingService mryTracingService, CustomizedObjectMapper objectMapper ) {
        this.mryTracingService = mryTracingService;
        this.objectMapper = objectMapper;
    }

    @Override
    public void handle( HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException ) throws IOException {
        SecurityContextHolder.clearContext( );
        response.setStatus( 403 );
        response.setContentType( APPLICATION_JSON_VALUE );
        response.setCharacterEncoding( UTF_8 );

        String traceId = mryTracingService.currentTraceId( );
        Error error = new Error( ACCESS_DENIED, 403, "Access denied.", request.getRequestURI( ), traceId, null );
        PrintWriter writer = response.getWriter( );
        writer.print( objectMapper.writeValueAsString( error.toErrorResponse( ) ) );
        writer.flush( );
    }
}
