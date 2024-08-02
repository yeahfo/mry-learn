package io.github.yeahfo.mry.learn.core.member.application.command;

import io.github.yeahfo.mry.learn.core.common.application.Command;
import io.github.yeahfo.mry.learn.core.common.exception.MryException;
import io.github.yeahfo.mry.learn.core.common.validation.password.Password;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

import java.util.Objects;

import static io.github.yeahfo.mry.learn.core.common.exception.ErrorCode.PASSWORD_CONFIRM_NOT_MATCH;

@Builder
public record ChangeMyPasswordCommand( @NotBlank
                                       @Password
                                       String oldPassword,
                                       @NotBlank
                                       @Password
                                       String newPassword,
                                       @NotBlank
                                       @Password
                                       String confirmNewPassword ) implements Command {
    @Override
    public void correctAndValidate( ) {
        if ( !Objects.equals( newPassword, confirmNewPassword ) ) {
            throw new MryException( PASSWORD_CONFIRM_NOT_MATCH, "修改密码失败，确认密码和新密码不一致。" );
        }
    }
}
