package io.github.yeahfo.mry.learn.core.tenant.application.representation;

import io.github.yeahfo.mry.learn.core.common.domain.UploadedFile;
import lombok.Builder;

@Builder
public record ConsoleTenantProfileRepresentation( String tenantId,
                                                  String name,
                                                  UploadedFile logo,
                                                  String subdomainPrefix,
                                                  String baseDomainName,
                                                  boolean subdomainReady,
                                                  PackagesStatusRepresentation packagesStatus ) {
}
