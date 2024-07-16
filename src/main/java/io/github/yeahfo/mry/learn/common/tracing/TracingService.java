package io.github.yeahfo.mry.learn.common.tracing;

import io.micrometer.tracing.ScopedSpan;
import io.micrometer.tracing.Span;
import io.micrometer.tracing.Tracer;
import org.springframework.stereotype.Component;

@Component
public class TracingService {
    private final Tracer tracer;

    public TracingService( Tracer tracer ) {
        this.tracer = tracer;
    }

    public String currentTraceId( ) {
        Span span = tracer.currentSpan( );
        return span != null ? span.context( ).traceId( ) : null;
    }

    public ScopedSpan startNewSpan( String name ) {
        return tracer.startScopedSpan( name );
    }
}
