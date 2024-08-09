package io.github.yeahfo.mry.learn.core.tenant;

import io.github.yeahfo.mry.learn.core.common.domain.User;
import io.github.yeahfo.mry.learn.core.tenant.application.TenantCommandService;
import io.github.yeahfo.mry.learn.core.tenant.application.command.UpdateTenantBaseSettingCommand;
import io.github.yeahfo.mry.learn.core.tenant.application.command.UpdateTenantLogoCommand;
import io.github.yeahfo.mry.learn.core.tenant.application.command.UpdateTenantSubdomainCommand;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static io.github.yeahfo.mry.learn.common.spring.SpringCommonConfiguration.AUTHORIZATION_BEARER_JWT;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping( "/tenants" )
@Tag( name = "Tenant", description = "Tenant APIs." )
public class TenantResource {
    private final TenantCommandService commandService;


    @ResponseStatus( NO_CONTENT )
    @SecurityRequirement( name = AUTHORIZATION_BEARER_JWT )
    @PutMapping( value = "/current/base-setting", consumes = APPLICATION_JSON_VALUE )
    public void updateTenantBaseSetting( @RequestBody @Valid UpdateTenantBaseSettingCommand command,
                                         @AuthenticationPrincipal User user ) {
        commandService.updateTenantBaseSetting( command, user );
    }

    @ResponseStatus( NO_CONTENT )
    @SecurityRequirement( name = AUTHORIZATION_BEARER_JWT )
    @PutMapping( value = "/current/logo", consumes = APPLICATION_JSON_VALUE )
    public void updateTenantLogo( @RequestBody @Valid UpdateTenantLogoCommand command,
                                  @AuthenticationPrincipal User user ) {
        commandService.updateTenantLogo( command, user );
    }

    @ResponseStatus( NO_CONTENT )
    @SecurityRequirement( name = AUTHORIZATION_BEARER_JWT )
    @PutMapping( value = "/current/subdomain", consumes = APPLICATION_JSON_VALUE )
    public void updateTenantSubdomain( @RequestBody @Valid UpdateTenantSubdomainCommand command,
                                       @AuthenticationPrincipal User user ) {
        commandService.updateTenantSubdomain( command, user );
    }
}
