package io.github.yeahfo.mry.learn.core.member.application.command;

import io.github.yeahfo.mry.learn.core.common.application.Command;
import io.github.yeahfo.mry.learn.core.common.validation.mobileoremail.MobileOrEmail;
import io.github.yeahfo.mry.learn.core.common.validation.password.Password;
import io.github.yeahfo.mry.learn.core.common.validation.verficationcode.VerificationCode;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record FindBackPasswordCommand( @NotBlank
                                       @MobileOrEmail
                                       String mobileOrEmail,
                                       @NotBlank
                                       @VerificationCode
                                       String verification,
                                       @NotBlank
                                       @Password
                                       String password ) implements Command {
}
