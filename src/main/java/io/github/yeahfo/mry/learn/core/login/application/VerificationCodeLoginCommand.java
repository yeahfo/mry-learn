package io.github.yeahfo.mry.learn.core.login.application;

import io.github.yeahfo.mry.learn.core.common.application.Command;
import io.github.yeahfo.mry.learn.core.common.validation.mobileoremail.MobileOrEmail;
import io.github.yeahfo.mry.learn.core.common.validation.verficationcode.VerificationCode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record VerificationCodeLoginCommand( @NotBlank
                                            @MobileOrEmail
                                            String mobileOrEmail,
                                            @NotBlank
                                            @VerificationCode
                                            String verification,
                                            @Size( max = 1000 )
                                            String wxIdInfo ) implements Command {
}
