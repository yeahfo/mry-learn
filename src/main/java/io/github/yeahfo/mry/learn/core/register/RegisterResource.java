package io.github.yeahfo.mry.learn.core.register;

import io.github.yeahfo.mry.learn.core.register.application.RegisterCommand;
import io.github.yeahfo.mry.learn.core.register.application.RegisterCommandService;
import io.github.yeahfo.mry.learn.core.register.application.RegisteredRepresentation;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.HttpStatus.CREATED;

@Validated
@RestController
@RequestMapping( "/registrations" )
public class RegisterResource {
    private final RegisterCommandService commandService;

    public RegisterResource( RegisterCommandService commandService ) {
        this.commandService = commandService;
    }

    @PostMapping
    public ResponseEntity< RegisteredRepresentation > register( @RequestBody @Valid RegisterCommand command ) {
        RegisteredRepresentation register = commandService.register( command );
        return ResponseEntity.status( CREATED ).body( register );
    }
}
