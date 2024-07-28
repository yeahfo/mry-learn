package io.github.yeahfo.mry.learn.core.verification.application;

import io.github.yeahfo.mry.learn.core.common.application.Command;
import io.github.yeahfo.mry.learn.core.common.validation.mobileoremail.MobileOrEmail;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record CreateLoginVerificationCodeCommand( @NotBlank
                                                  @MobileOrEmail
                                                  String mobileOrEmail ) implements Command {
}
