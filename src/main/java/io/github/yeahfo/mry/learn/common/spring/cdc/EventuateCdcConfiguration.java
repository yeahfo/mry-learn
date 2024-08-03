package io.github.yeahfo.mry.learn.common.spring.cdc;

import io.eventuate.tram.cdc.connector.pipeline.CdcTramPipelineConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile( {"Redis", "PostgresWal"} )
@Import( { CdcTramPipelineConfiguration.class } )
public class EventuateCdcConfiguration {
}
