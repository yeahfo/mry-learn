package io.github.yeahfo.mry.learn.core.member;

import io.github.yeahfo.mry.learn.core.common.application.PagedRepresentation;
import io.github.yeahfo.mry.learn.core.common.domain.User;
import io.github.yeahfo.mry.learn.core.common.utils.IdentifierRepresentation;
import io.github.yeahfo.mry.learn.core.common.validation.id.app.AppId;
import io.github.yeahfo.mry.learn.core.common.validation.id.member.MemberId;
import io.github.yeahfo.mry.learn.core.common.validation.id.tenant.TenantId;
import io.github.yeahfo.mry.learn.core.member.application.*;
import io.github.yeahfo.mry.learn.core.member.application.command.*;
import io.github.yeahfo.mry.learn.core.member.application.representation.*;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

import static io.github.yeahfo.mry.learn.common.spring.SpringCommonConfiguration.AUTHORIZATION_BEARER_JWT;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;
import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON_VALUE;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping( value = "/members" )
@Tag( name = "Member", description = "Member APIs")
public class MemberResource {
    private final MemberApplicationService applicationService;
    private final MemberProfileRepresentationService representationService;


    @ResponseStatus( CREATED )
    @SecurityRequirement( name = AUTHORIZATION_BEARER_JWT )
    @PostMapping( consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE )
    public ResponseEntity< IdentifierRepresentation > createMember( @RequestBody @Valid CreateMemberCommand command,
                                                                    @AuthenticationPrincipal User user ) {
        String createdId = applicationService.createMember( command, user );
        return ResponseEntity.status( CREATED ).body( new IdentifierRepresentation( createdId ) );
    }

    @SecurityRequirement( name = AUTHORIZATION_BEARER_JWT )
    @PostMapping( value = "/import", consumes = MULTIPART_FORM_DATA_VALUE, produces = APPLICATION_JSON_VALUE )
    public ResponseEntity< MemberImportRepresentation > importMembers( @RequestParam( "file" ) @NotNull MultipartFile file,
                                                                       @AuthenticationPrincipal User user ) throws IOException {
        MemberImportRepresentation imported = applicationService.importMembers( file.getInputStream( ), user );
        return ResponseEntity.ok( imported );
    }

    @ResponseStatus( NO_CONTENT )
    @SecurityRequirement( name = AUTHORIZATION_BEARER_JWT )
    @PutMapping( value = "/{memberId}", consumes = APPLICATION_JSON_VALUE )
    public ResponseEntity< Void > updateMember( @PathVariable @NotBlank @MemberId String memberId,
                                                @RequestBody @Valid UpdateMemberInfoCommand command,
                                                @AuthenticationPrincipal User user ) {
        applicationService.updateMember( memberId, command, user );
        return ResponseEntity.noContent( ).build( );
    }

    @ResponseStatus( NO_CONTENT )
    @DeleteMapping( value = "/{memberId}" )
    @SecurityRequirement( name = AUTHORIZATION_BEARER_JWT )
    public ResponseEntity< Void > deleteMember( @PathVariable( "memberId" ) @NotBlank @MemberId String memberId,
                                                @AuthenticationPrincipal User user ) {
        applicationService.deleteMember( memberId, user );
        return ResponseEntity.noContent( ).build( );
    }

    @ResponseStatus( NO_CONTENT )
    @SecurityRequirement( name = AUTHORIZATION_BEARER_JWT )
    @PutMapping( value = "/{memberId}/role", consumes = APPLICATION_JSON_VALUE )
    public ResponseEntity< Void > updateMemberRole( @PathVariable @NotBlank @MemberId String memberId,
                                                    @RequestBody @Valid UpdateMemberRoleCommand command,
                                                    @AuthenticationPrincipal User user ) {
        applicationService.updateMemberRole( memberId, command, user );
        return ResponseEntity.noContent( ).build( );
    }

    @ResponseStatus( NO_CONTENT )
    @PutMapping( value = "/{memberId}/activation" )
    @SecurityRequirement( name = AUTHORIZATION_BEARER_JWT )
    public ResponseEntity< Void > activateMember( @PathVariable( "memberId" ) @NotBlank @MemberId String memberId,
                                                  @AuthenticationPrincipal User user ) {
        applicationService.activateMember( memberId, user );
        return ResponseEntity.noContent( ).build( );
    }

    @ResponseStatus( NO_CONTENT )
    @PutMapping( value = "/{memberId}/deactivation" )
    @SecurityRequirement( name = AUTHORIZATION_BEARER_JWT )
    public ResponseEntity< Void > deactivateMember( @PathVariable( "memberId" ) @NotBlank @MemberId String memberId,
                                                    @AuthenticationPrincipal User user ) {
        applicationService.deactivateMember( memberId, user );
        return ResponseEntity.noContent( ).build( );
    }

    @ResponseStatus( NO_CONTENT )
    @SecurityRequirement( name = AUTHORIZATION_BEARER_JWT )
    @PutMapping( value = "/{memberId}/password", consumes = APPLICATION_JSON_VALUE )
    public ResponseEntity< Void > resetPasswordForMember( @PathVariable( "memberId" ) @NotBlank @MemberId String memberId,
                                                          @RequestBody @Valid ResetMemberPasswordCommand command,
                                                          @AuthenticationPrincipal User user ) {
        applicationService.resetPasswordForMember( memberId, command, user );
        return ResponseEntity.noContent( ).build( );
    }

    @ResponseStatus( NO_CONTENT )
    @SecurityRequirement( name = AUTHORIZATION_BEARER_JWT )
    @DeleteMapping( value = "/{memberId}/wx", consumes = APPLICATION_JSON_VALUE )
    public ResponseEntity< Void > unbindMemberWx( @PathVariable( "memberId" ) @NotBlank @MemberId String memberId,
                                                  @AuthenticationPrincipal User user ) {
        applicationService.unbindMemberWx( memberId, user );
        return ResponseEntity.noContent( ).build( );
    }

    @ResponseStatus( NO_CONTENT )
    @SecurityRequirement( name = AUTHORIZATION_BEARER_JWT )
    @PutMapping( value = "/me/password", consumes = APPLICATION_JSON_VALUE )
    public ResponseEntity< Void > changeMyPassword( @RequestBody @Valid ChangeMyPasswordCommand command,
                                                    @AuthenticationPrincipal User user ) {
        applicationService.changeMyPassword( command, user );
        return ResponseEntity.noContent( ).build( );
    }

    @ResponseStatus( NO_CONTENT )
    @SecurityRequirement( name = AUTHORIZATION_BEARER_JWT )
    @PutMapping( value = "/me/mobile", consumes = APPLICATION_JSON_VALUE )
    public ResponseEntity< Void > changeMyMobile( @RequestBody @Valid ChangeMyMobileCommand command,
                                                  @AuthenticationPrincipal User user ) {
        applicationService.changeMyMobile( command, user );
        return ResponseEntity.noContent( ).build( );
    }

    @ResponseStatus( NO_CONTENT )
    @SecurityRequirement( name = AUTHORIZATION_BEARER_JWT )
    @PutMapping( value = "/me/mobile-identification", consumes = APPLICATION_JSON_VALUE )
    public ResponseEntity< Void > identifyMyMobile( @RequestBody @Valid IdentifyMyMobileCommand command,
                                                    @AuthenticationPrincipal User user ) {
        applicationService.identifyMyMobile( command, user );
        return ResponseEntity.noContent( ).build( );
    }

    @ResponseStatus( NO_CONTENT )
    @SecurityRequirement( name = AUTHORIZATION_BEARER_JWT )
    @PutMapping( value = "/me/base-setting", consumes = APPLICATION_JSON_VALUE )
    public ResponseEntity< Void > updateMyBaseSetting( @RequestBody @Valid UpdateMyBaseSettingCommand command,
                                                       @AuthenticationPrincipal User user ) {
        applicationService.updateMyBaseSetting( command, user );
        return ResponseEntity.noContent( ).build( );
    }

    @ResponseStatus( NO_CONTENT )
    @SecurityRequirement( name = AUTHORIZATION_BEARER_JWT )
    @PutMapping( value = "/me/avatar", consumes = APPLICATION_JSON_VALUE )
    public ResponseEntity< Void > updateMyAvatar( @RequestBody @Valid UpdateMyAvatarCommand command,
                                                  @AuthenticationPrincipal User user ) {
        applicationService.updateMyAvatar( command, user );
        return ResponseEntity.noContent( ).build( );
    }

    @ResponseStatus( NO_CONTENT )
    @DeleteMapping( value = "/me/avatar" )
    @SecurityRequirement( name = AUTHORIZATION_BEARER_JWT )
    public ResponseEntity< Void > deleteMyAvatar( @AuthenticationPrincipal User user ) {
        applicationService.deleteMyAvatar( user );
        return ResponseEntity.noContent( ).build( );
    }

    @ResponseStatus( NO_CONTENT )
    @DeleteMapping( value = "/me/wx" )
    @SecurityRequirement( name = AUTHORIZATION_BEARER_JWT )
    public ResponseEntity< Void > unbindMyWx( @AuthenticationPrincipal User user ) {
        applicationService.unbindMyWx( user );
        return ResponseEntity.noContent( ).build( );
    }

    @ResponseStatus( NO_CONTENT )
    @PostMapping( value = "/find-back-password", consumes = APPLICATION_JSON_VALUE )
    public ResponseEntity< Void > findBackPassword( @RequestBody @Valid FindBackPasswordCommand command ) {
        applicationService.findBackPassword( command );
        return ResponseEntity.noContent( ).build( );
    }

    @ResponseStatus( NO_CONTENT )
    @PutMapping( value = "/me/top-apps/{appid}" )
    @SecurityRequirement( name = AUTHORIZATION_BEARER_JWT )
    public ResponseEntity< Void > topApp( @PathVariable( "appid" ) @NotBlank @AppId String appId,
                                          @AuthenticationPrincipal User user ) {
        applicationService.topApp( appId, user );
        return ResponseEntity.noContent( ).build( );
    }

    @ResponseStatus( NO_CONTENT )
    @DeleteMapping( value = "/me/top-apps/{appid}" )
    @SecurityRequirement( name = AUTHORIZATION_BEARER_JWT )
    public ResponseEntity< Void > cancelTopApp( @PathVariable( "appid" ) @NotBlank @AppId String appid,
                                                @AuthenticationPrincipal User user ) {
        applicationService.cancelTopApp( appid, user );
        return ResponseEntity.noContent( ).build( );
    }

    @SecurityRequirement( name = AUTHORIZATION_BEARER_JWT )
    @GetMapping( value = "/me", produces = APPLICATION_JSON_VALUE )
    public ResponseEntity< ConsoleMemberProfileRepresentation > fetchMyProfile( @AuthenticationPrincipal User user ) {
        ConsoleMemberProfileRepresentation profile = representationService.fetchMyProfile( user );
        return ResponseEntity.ok( profile );
    }

    @SecurityRequirement( name = AUTHORIZATION_BEARER_JWT )
    @GetMapping( value = "/client/me", produces = APPLICATION_JSON_VALUE )
    public ResponseEntity< ClientMemberProfileRepresentation > fetchMyClientProfile( @AuthenticationPrincipal User user ) {
        return ResponseEntity.ok( representationService.fetchMyClientMemberProfile( user ) );
    }

    @SecurityRequirement( name = AUTHORIZATION_BEARER_JWT )
    @GetMapping( value = "/me/info", produces = APPLICATION_JSON_VALUE )
    public ResponseEntity< MemberInfoRepresentation > fetchMyMemberInfo( @AuthenticationPrincipal User user ) {
        return ResponseEntity.ok( representationService.fetchMyMemberInfo( user ) );
    }

    @SecurityRequirement( name = AUTHORIZATION_BEARER_JWT )
    @GetMapping( value = "/me/base-setting", produces = APPLICATION_JSON_VALUE )
    public ResponseEntity< MemberBaseSettingRepresentation > fetchMyBaseSetting( @AuthenticationPrincipal User user ) {
        return ResponseEntity.ok( representationService.fetchMyBaseSetting( user ) );
    }

    @SecurityRequirement( name = AUTHORIZATION_BEARER_JWT )
    @PostMapping( value = "/my-managed-members", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE )
    public ResponseEntity< PagedRepresentation< ListMemberRepresentation > > listMyManagedMembers( @RequestBody @Valid ListMyManagedMembersCommand command,
                                                                                                   @AuthenticationPrincipal User user ) {
        return ResponseEntity.ok( representationService.listMyManagedMembers( command, user ) );
    }

    @SecurityRequirement( name = AUTHORIZATION_BEARER_JWT )
    @GetMapping( value = "/all-references", produces = APPLICATION_JSON_VALUE )
    public ResponseEntity< List< MemberReferenceRepresentation > > listMemberReferences( @AuthenticationPrincipal User user ) {
        return ResponseEntity.ok( representationService.listMemberReferences( user ) );
    }

    @SecurityRequirement( name = AUTHORIZATION_BEARER_JWT )
    @GetMapping( value = "/all-references/{tenantId}", produces = APPLICATION_JSON_VALUE )
    public ResponseEntity< List< MemberReferenceRepresentation > > listMemberReferencesForTenant( @PathVariable @NotBlank @TenantId String tenantId,
                                                                                                  @AuthenticationPrincipal User user ) {
        return ResponseEntity.ok( representationService.listMemberReferencesForTenant( tenantId, user ) );
    }
}
