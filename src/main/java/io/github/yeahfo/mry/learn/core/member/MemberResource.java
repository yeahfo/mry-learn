package io.github.yeahfo.mry.learn.core.member;

import io.github.yeahfo.mry.learn.core.common.domain.User;
import io.github.yeahfo.mry.learn.core.common.utils.IdentifierRepresentation;
import io.github.yeahfo.mry.learn.core.common.validation.id.app.AppId;
import io.github.yeahfo.mry.learn.core.common.validation.id.member.MemberId;
import io.github.yeahfo.mry.learn.core.member.application.*;
import io.github.yeahfo.mry.learn.core.member.application.command.*;
import io.github.yeahfo.mry.learn.core.member.application.representation.*;
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

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;
import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON_VALUE;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping( value = "/members" )
public class MemberResource {
    private final MemberApplicationService applicationService;
    private final MemberProfileRepresentationService representationService;


    @ResponseStatus( CREATED )
    @PostMapping( consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE )
    public ResponseEntity< IdentifierRepresentation > createMember( @RequestBody @Valid CreateMemberCommand command,
                                                                    @AuthenticationPrincipal User user ) {
        String createdId = applicationService.createMember( command, user );
        return ResponseEntity.status( CREATED ).body( new IdentifierRepresentation( createdId ) );
    }

    @PostMapping( value = "/import", consumes = MULTIPART_FORM_DATA_VALUE, produces = APPLICATION_JSON_VALUE )
    public ResponseEntity< MemberImportRepresentation > importMembers( @RequestParam( "file" ) @NotNull MultipartFile file,
                                                                       @AuthenticationPrincipal User user ) throws IOException {
        MemberImportRepresentation imported = applicationService.importMembers( file.getInputStream( ), user );
        return ResponseEntity.ok( imported );
    }

    @ResponseStatus( NO_CONTENT )
    @PutMapping( value = "/{memberId}", consumes = APPLICATION_JSON_VALUE )
    public ResponseEntity< Void > updateMember( @PathVariable @NotBlank @MemberId String memberId,
                                                @RequestBody @Valid UpdateMemberInfoCommand command,
                                                @AuthenticationPrincipal User user ) {
        applicationService.updateMember( memberId, command, user );
        return ResponseEntity.noContent( ).build( );
    }

    @ResponseStatus( NO_CONTENT )
    @DeleteMapping( value = "/{memberId}" )
    public ResponseEntity< Void > deleteMember( @PathVariable( "memberId" ) @NotBlank @MemberId String memberId,
                                                @AuthenticationPrincipal User user ) {
        applicationService.deleteMember( memberId, user );
        return ResponseEntity.noContent( ).build( );
    }

    @ResponseStatus( NO_CONTENT )
    @PutMapping( value = "/{memberId}/role", consumes = APPLICATION_JSON_VALUE )
    public ResponseEntity< Void > updateMemberRole( @PathVariable @NotBlank @MemberId String memberId,
                                                    @RequestBody @Valid UpdateMemberRoleCommand command,
                                                    @AuthenticationPrincipal User user ) {
        applicationService.updateMemberRole( memberId, command, user );
        return ResponseEntity.noContent( ).build( );
    }

    @ResponseStatus( NO_CONTENT )
    @PutMapping( value = "/{memberId}/activation" )
    public ResponseEntity< Void > activateMember( @PathVariable( "memberId" ) @NotBlank @MemberId String memberId,
                                                  @AuthenticationPrincipal User user ) {
        applicationService.activateMember( memberId, user );
        return ResponseEntity.noContent( ).build( );
    }

    @ResponseStatus( NO_CONTENT )
    @PutMapping( value = "/{memberId}/deactivation" )
    public ResponseEntity< Void > deactivateMember( @PathVariable( "memberId" ) @NotBlank @MemberId String memberId,
                                                    @AuthenticationPrincipal User user ) {
        applicationService.deactivateMember( memberId, user );
        return ResponseEntity.noContent( ).build( );
    }

    @ResponseStatus( NO_CONTENT )
    @PutMapping( value = "/{memberId}/password", consumes = APPLICATION_JSON_VALUE )
    public ResponseEntity< Void > resetPasswordForMember( @PathVariable( "memberId" ) @NotBlank @MemberId String memberId,
                                                          @RequestBody @Valid ResetMemberPasswordCommand command,
                                                          @AuthenticationPrincipal User user ) {
        applicationService.resetPasswordForMember( memberId, command, user );
        return ResponseEntity.noContent( ).build( );
    }

    @ResponseStatus( NO_CONTENT )
    @DeleteMapping( value = "/{memberId}/wx", consumes = APPLICATION_JSON_VALUE )
    public ResponseEntity< Void > unbindMemberWx( @PathVariable( "memberId" ) @NotBlank @MemberId String memberId,
                                                  @AuthenticationPrincipal User user ) {
        applicationService.unbindMemberWx( memberId, user );
        return ResponseEntity.noContent( ).build( );
    }

    @ResponseStatus( NO_CONTENT )
    @PutMapping( value = "/me/password", consumes = APPLICATION_JSON_VALUE )
    public ResponseEntity< Void > changeMyPassword( @RequestBody @Valid ChangeMyPasswordCommand command,
                                                    @AuthenticationPrincipal User user ) {
        applicationService.changeMyPassword( command, user );
        return ResponseEntity.noContent( ).build( );
    }

    @ResponseStatus( NO_CONTENT )
    @PutMapping( value = "/me/mobile", consumes = APPLICATION_JSON_VALUE )
    public ResponseEntity< Void > changeMyMobile( @RequestBody @Valid ChangeMyMobileCommand command,
                                                  @AuthenticationPrincipal User user ) {
        applicationService.changeMyMobile( command, user );
        return ResponseEntity.noContent( ).build( );
    }

    @ResponseStatus( NO_CONTENT )
    @PutMapping( value = "/me/mobile-identification", consumes = APPLICATION_JSON_VALUE )
    public ResponseEntity< Void > identifyMyMobile( @RequestBody @Valid IdentifyMyMobileCommand command,
                                                    @AuthenticationPrincipal User user ) {
        applicationService.identifyMyMobile( command, user );
        return ResponseEntity.noContent( ).build( );
    }

    @ResponseStatus( NO_CONTENT )
    @PutMapping( value = "/me/base-setting", consumes = APPLICATION_JSON_VALUE )
    public ResponseEntity< Void > updateMyBaseSetting( @RequestBody @Valid UpdateMyBaseSettingCommand command,
                                                       @AuthenticationPrincipal User user ) {
        applicationService.updateMyBaseSetting( command, user );
        return ResponseEntity.noContent( ).build( );
    }

    @ResponseStatus( NO_CONTENT )
    @PutMapping( value = "/me/avatar", consumes = APPLICATION_JSON_VALUE )
    public ResponseEntity< Void > updateMyAvatar( @RequestBody @Valid UpdateMyAvatarCommand command,
                                                  @AuthenticationPrincipal User user ) {
        applicationService.updateMyAvatar( command, user );
        return ResponseEntity.noContent( ).build( );
    }

    @ResponseStatus( NO_CONTENT )
    @DeleteMapping( value = "/me/avatar" )
    public ResponseEntity< Void > deleteMyAvatar( @AuthenticationPrincipal User user ) {
        applicationService.deleteMyAvatar( user );
        return ResponseEntity.noContent( ).build( );
    }

    @ResponseStatus( NO_CONTENT )
    @DeleteMapping( value = "/me/wx" )
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
    public ResponseEntity< Void > topApp( @PathVariable( "appid" ) @NotBlank @AppId String appId,
                                          @AuthenticationPrincipal User user ) {
        applicationService.topApp( appId, user );
        return ResponseEntity.noContent( ).build( );
    }

    @ResponseStatus( NO_CONTENT )
    @DeleteMapping( value = "/me/top-apps/{appid}" )
    public ResponseEntity< Void > cancelTopApp( @PathVariable( "appid" ) @NotBlank @AppId String appid,
                                                @AuthenticationPrincipal User user ) {
        applicationService.cancelTopApp( appid, user );
        return ResponseEntity.noContent( ).build( );
    }

    @GetMapping( value = "/me", produces = APPLICATION_JSON_VALUE )
    public ResponseEntity< ConsoleMemberProfileRepresentation > fetchMyProfile( @AuthenticationPrincipal User user ) {
        ConsoleMemberProfileRepresentation profile = representationService.fetchMyProfile( user );
        return ResponseEntity.ok( profile );
    }

    @GetMapping( value = "/client/me", produces = APPLICATION_JSON_VALUE )
    public ResponseEntity< ClientMemberProfileRepresentation > fetchMyClientProfile( @AuthenticationPrincipal User user ) {
        return ResponseEntity.ok( representationService.fetchMyClientMemberProfile( user ) );
    }

    @GetMapping( value = "/me/info", produces = APPLICATION_JSON_VALUE )
    public ResponseEntity< MemberInfoRepresentation > fetchMyMemberInfo( @AuthenticationPrincipal User user ) {
        return ResponseEntity.ok( representationService.fetchMyMemberInfo( user ) );
    }

    @GetMapping( value = "/me/base-setting", produces = APPLICATION_JSON_VALUE )
    public ResponseEntity< MemberBaseSettingRepresentation > fetchMyBaseSetting( @AuthenticationPrincipal User user ) {
        return ResponseEntity.ok( representationService.fetchMyBaseSetting( user ) );
    }

//    @PostMapping( value = "/my-managed-members", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE )
//    public PagedList< QListMember > listMyManagedMembers( @RequestBody @Valid ListMyManagedMembersQuery queryCommand,
//                                                          @AuthenticationPrincipal User user ) {
//        return memberQueryService.listMyManagedMembers( queryCommand, user );
//    }
}
