package io.github.yeahfo.mry.learn.management;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

@Component
public class SystemInitializer implements ApplicationListener< ApplicationReadyEvent > {
    private final SystemManageTenant systemManageTenant;

    public SystemInitializer( SystemManageTenant systemManageTenant ) {
        this.systemManageTenant = systemManageTenant;
    }

    @Override
    public void onApplicationEvent( @NonNull ApplicationReadyEvent event ) {
        ensureManageAppsExists( );
    }

    private void ensureManageAppsExists( ) {
        //码如云管理
        systemManageTenant.init( );
    }
}
