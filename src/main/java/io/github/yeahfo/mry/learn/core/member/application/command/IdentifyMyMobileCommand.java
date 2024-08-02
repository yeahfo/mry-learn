package io.github.yeahfo.mry.learn.core.member.application.command;

import io.github.yeahfo.mry.learn.core.common.application.Command;
import io.github.yeahfo.mry.learn.core.common.validation.mobile.Mobile;
import io.github.yeahfo.mry.learn.core.common.validation.verficationcode.VerificationCode;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record IdentifyMyMobileCommand( @Mobile
                                       @NotBlank
                                       String mobile,
                                       @NotBlank
                                       @VerificationCode
                                       String verification ) implements Command {
}
