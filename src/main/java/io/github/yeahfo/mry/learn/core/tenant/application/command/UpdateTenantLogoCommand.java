package io.github.yeahfo.mry.learn.core.tenant.application.command;

import io.github.yeahfo.mry.learn.core.common.application.Command;
import io.github.yeahfo.mry.learn.core.common.domain.UploadedFile;
import jakarta.validation.Valid;
import lombok.Builder;

@Builder
public record UpdateTenantLogoCommand( @Valid UploadedFile logo ) implements Command {
}
