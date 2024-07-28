package io.github.yeahfo.mry.learn.core.verification.application;

import io.github.yeahfo.mry.learn.core.common.application.Command;
import io.github.yeahfo.mry.learn.core.common.validation.mobile.Mobile;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record CreateChangeMobileVerificationCodeCommand( @NotBlank
                                                         @Mobile
                                                         @Size( max = 20 )
                                                         String mobile ) implements Command {
}
