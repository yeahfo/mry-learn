package io.github.yeahfo.mry.learn.core.member.application.command;

import io.github.yeahfo.mry.learn.core.common.application.Command;
import io.github.yeahfo.mry.learn.core.common.validation.password.Password;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record ResetMemberPasswordCommand( @NotNull
                                          @Password String password ) implements Command {
}
