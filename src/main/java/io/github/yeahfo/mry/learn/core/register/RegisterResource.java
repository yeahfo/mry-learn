package io.github.yeahfo.mry.learn.core.register;

import io.github.yeahfo.mry.learn.core.register.application.RegisterCommand;
import io.github.yeahfo.mry.learn.core.register.application.RegisterApplicationService;
import io.github.yeahfo.mry.learn.core.register.application.RegisteredRepresentation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Validated
@RestController
@RequestMapping( "/registrations" )
@Tag( name = "Registration", description = "Registration APIs" )
public class RegisterResource {
    private final RegisterApplicationService applicationService;

    public RegisterResource( RegisterApplicationService applicationService ) {
        this.applicationService = applicationService;
    }

    @ResponseStatus( CREATED )
    @PostMapping( consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE )
    public ResponseEntity< RegisteredRepresentation > register( @RequestBody @Valid RegisterCommand command ) {
        RegisteredRepresentation register = applicationService.register( command );
        return ResponseEntity.ofNullable( register );
    }
}
