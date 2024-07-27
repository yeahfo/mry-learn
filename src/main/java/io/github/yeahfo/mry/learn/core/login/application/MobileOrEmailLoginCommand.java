package io.github.yeahfo.mry.learn.core.login.application;

import io.github.yeahfo.mry.learn.core.common.application.Command;
import io.github.yeahfo.mry.learn.core.common.validation.mobileoremail.MobileOrEmail;
import io.github.yeahfo.mry.learn.core.common.validation.password.Password;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record MobileOrEmailLoginCommand( @NotBlank @MobileOrEmail String mobileOrEmail,
                                         @NotBlank @Password String password,
                                         @Size( max = 1000 ) String wxIdInfo ) implements Command {
}
