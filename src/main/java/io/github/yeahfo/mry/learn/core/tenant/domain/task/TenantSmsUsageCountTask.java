package io.github.yeahfo.mry.learn.core.tenant.domain.task;

import io.github.yeahfo.mry.learn.core.common.domain.task.OnetimeTask;
import io.github.yeahfo.mry.learn.core.tenant.domain.TenantRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class TenantSmsUsageCountTask implements OnetimeTask {
    private final TenantRepository tenantRepository;

    @Transactional
    public void run( String tenantId ) {
        tenantRepository.findById( tenantId ).ifPresent( tenant -> {
            tenant.useSms( );
            tenantRepository.save( tenant );
        } );
    }
}
