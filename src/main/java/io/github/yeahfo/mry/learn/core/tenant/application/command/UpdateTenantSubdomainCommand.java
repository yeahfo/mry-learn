package io.github.yeahfo.mry.learn.core.tenant.application.command;

import io.github.yeahfo.mry.learn.core.common.application.Command;
import io.github.yeahfo.mry.learn.core.common.validation.nospace.NoSpace;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import static io.github.yeahfo.mry.learn.core.common.utils.MryConstants.MAX_SUBDOMAIN_LENGTH;
import static io.github.yeahfo.mry.learn.core.common.utils.MryConstants.MIN_SUBDOMAIN_LENGTH;
import static io.github.yeahfo.mry.learn.core.common.utils.MryRegexConstants.SUBDOMAIN_PATTERN;

public record UpdateTenantSubdomainCommand( @NoSpace
                                            @Size( min = MIN_SUBDOMAIN_LENGTH, max = MAX_SUBDOMAIN_LENGTH )
                                            @Pattern( regexp = SUBDOMAIN_PATTERN, message = "子域名格式不正确" )
                                            String subdomainPrefix ) implements Command {
}
