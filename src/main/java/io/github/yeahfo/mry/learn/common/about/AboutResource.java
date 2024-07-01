package io.github.yeahfo.mry.learn.common.about;

import io.github.yeahfo.mry.learn.common.ratelimit.MRYRateLimiter;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.ZonedDateTime;

@RestController
@RequestMapping
public class AboutResource {
    private final ZonedDateTime deployTime = ZonedDateTime.now( );
    private final Environment environment;
    private final MRYRateLimiter rateLimiter;

    public AboutResource( Environment environment, MRYRateLimiter rateLimiter ) {
        this.environment = environment;
        this.rateLimiter = rateLimiter;
    }

    @GetMapping( "/about" )
    public ResponseEntity< QAboutInfo > about( ) {
        rateLimiter.applyFor( "About", 10 );
        String buildTime = environment.getProperty( "buildTime" );
        String gitRevision = environment.getProperty( "gitRevision" );
        String gitBranch = environment.getProperty( "gitBranch" );
        String environment = this.environment.getActiveProfiles( )[ 0 ];
        String deployTime = this.deployTime.toString( );
        return ResponseEntity.ok( QAboutInfo.builder( )
                .buildTime( buildTime )
                .deployTime( deployTime )
                .gitRevision( gitRevision )
                .gitBranch( gitBranch )
                .environment( environment )
                .build( ) );
    }

    @GetMapping( "/favicon.ico" )
    public void dummyFavicon( ) {
        //nop
    }

}
