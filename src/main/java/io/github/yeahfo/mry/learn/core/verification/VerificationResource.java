package io.github.yeahfo.mry.learn.core.verification;

import io.github.yeahfo.mry.learn.core.common.utils.IdentifierRepresentation;
import io.github.yeahfo.mry.learn.core.verification.application.CreateRegisterVerificationCodeCommand;
import io.github.yeahfo.mry.learn.core.verification.application.VerificationCodeCommandService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static io.github.yeahfo.mry.learn.core.common.utils.IdentifierRepresentation.identifier;
import static org.springframework.http.HttpStatus.CREATED;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping( "/verification-codes" )
public class VerificationResource {
    private final VerificationCodeCommandService commandService;

    @PostMapping( value = "/for-register" )
    public ResponseEntity< IdentifierRepresentation > createVerificationCodeForRegister( @RequestBody
                                                                                         @Valid CreateRegisterVerificationCodeCommand command ) {
        String verificationCodeFoId = commandService.createVerificationCodeForRegister( command );
        return ResponseEntity.status( CREATED ).body( identifier( verificationCodeFoId ) );
    }
}
