package io.github.yeahfo.mry.learn.core.register.application;

import io.github.yeahfo.mry.learn.core.common.application.Command;
import io.github.yeahfo.mry.learn.core.common.exception.MryException;
import io.github.yeahfo.mry.learn.core.common.validation.mobileoremail.MobileOrEmail;
import io.github.yeahfo.mry.learn.core.common.validation.password.Password;
import io.github.yeahfo.mry.learn.core.common.validation.verficationcode.VerificationCode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import static io.github.yeahfo.mry.learn.core.common.exception.ErrorCode.MUST_SIGN_AGREEMENT;
import static io.github.yeahfo.mry.learn.core.common.utils.MryConstants.MAX_GENERIC_NAME_LENGTH;

public record RegisterCommand( @NotBlank
                               @MobileOrEmail
                               String mobileOrEmail,
                               @NotBlank
                               @VerificationCode
                               String verification,
                               @NotBlank
                               @Password
                               String password,
                               @NotBlank
                               @Size( max = MAX_GENERIC_NAME_LENGTH )
                               String username,
                               @NotBlank
                               @Size( max = MAX_GENERIC_NAME_LENGTH )
                               String tenantName,
                               boolean agreement ) implements Command {
    @Override
    public void correctAndValidate( ) {
        if ( !agreement ) {
            throw new MryException( MUST_SIGN_AGREEMENT, "请同意用户协议以完成注册。" );
        }
    }
}
