package io.github.yeahfo.mry.learn.core.tenant.application.command;

import io.github.yeahfo.mry.learn.core.common.application.Command;
import io.github.yeahfo.mry.learn.core.common.domain.UploadedFile;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import static io.github.yeahfo.mry.learn.core.common.utils.MryConstants.MAX_GENERIC_NAME_LENGTH;

@Builder
public record UpdateTenantBaseSettingCommand( @NotBlank
                                              @Size( max = MAX_GENERIC_NAME_LENGTH )
                                              String name,
                                              @Valid
                                              UploadedFile loginBackground ) implements Command {
}
