package io.github.yeahfo.mry.learn.core.verification;

import io.github.yeahfo.mry.learn.core.common.domain.User;
import io.github.yeahfo.mry.learn.core.common.utils.IdentifierRepresentation;
import io.github.yeahfo.mry.learn.core.verification.application.*;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static io.github.yeahfo.mry.learn.common.spring.SpringCommonConfiguration.AUTHORIZATION_BEARER_JWT;
import static io.github.yeahfo.mry.learn.core.common.utils.IdentifierRepresentation.identifier;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping( "/verification-codes" )
@Tag( name = "Verification code", description = "Create various types of verification codes" )
public class VerificationResource {
    private final VerificationCodeApplicationService applicationService;

    @PostMapping( value = "/for-register", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE )
    public ResponseEntity< IdentifierRepresentation > createVerificationCodeForRegister(
            @RequestBody @Valid CreateRegisterVerificationCodeCommand command ) {
        String verificationCodeId = applicationService.createVerificationCodeForRegister( command );
        return ResponseEntity.status( CREATED ).body( identifier( verificationCodeId ) );
    }

    @PostMapping( value = "/for-login",
            consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE )
    public ResponseEntity< IdentifierRepresentation > createVerificationCodeForLogin(
            @RequestBody @Valid CreateLoginVerificationCodeCommand command ) {
        String verificationCodeId = applicationService.createVerificationCodeForLogin( command );
        return ResponseEntity.status( CREATED ).body( identifier( verificationCodeId ) );
    }

    @PostMapping( value = "/for-find-back-password", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE )
    public ResponseEntity< IdentifierRepresentation > createVerificationCodeForFindBackPassword(
            @RequestBody @Valid CreateFindBackPasswordVerificationCodeCommand command ) {
        String verificationCodeId = applicationService.createVerificationCodeForFindBackPassword( command );
        return ResponseEntity.status( CREATED ).body( identifier( verificationCodeId ) );
    }

    @SecurityRequirement( name = AUTHORIZATION_BEARER_JWT )
    @PostMapping( value = "/for-change-mobile", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE )
    public ResponseEntity< IdentifierRepresentation > createVerificationCodeForChangeMobile(
            @RequestBody @Valid CreateChangeMobileVerificationCodeCommand command,
            @AuthenticationPrincipal User user ) {
        String verificationCodeId = applicationService.createVerificationCodeForChangeMobile( command, user );
        return ResponseEntity.status( CREATED ).body( identifier( verificationCodeId ) );
    }

    @SecurityRequirement( name = AUTHORIZATION_BEARER_JWT )
    @PostMapping( value = "/for-identity-mobile", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE )
    public ResponseEntity< IdentifierRepresentation > createVerificationCodeForIdentifyMobile(
            @RequestBody @Valid IdentifyMobileVerificationCodeCommand command,
            @AuthenticationPrincipal User user ) {
        String verificationCodeId = applicationService.createVerificationCodeForIdentifyMobile( command, user );
        return ResponseEntity.status( CREATED ).body( identifier( verificationCodeId ) );
    }

}

