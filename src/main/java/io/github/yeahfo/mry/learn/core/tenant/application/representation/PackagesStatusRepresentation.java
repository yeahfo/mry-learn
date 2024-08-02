package io.github.yeahfo.mry.learn.core.tenant.application.representation;

import io.github.yeahfo.mry.learn.core.app.domain.page.control.ControlType;
import io.github.yeahfo.mry.learn.core.plan.domain.PlanType;
import lombok.Builder;

import java.time.Instant;
import java.util.Set;

@Builder
public record PackagesStatusRepresentation( String planName,
                                            PlanType planType,
                                            String effectivePlanName,
                                            PlanType effectivePlanType,
                                            boolean maxAppReached,
                                            boolean maxMemberReached,
                                            boolean submissionNotifyAllowed,
                                            boolean batchImportQrAllowed,
                                            boolean batchImportMemberAllowed,
                                            boolean submissionApprovalAllowed,
                                            boolean reportingAllowed,
                                            boolean customSubdomainAllowed,
                                            boolean customLogoAllowed,
                                            boolean developerAllowed,
                                            boolean videoAudioAllowed,
                                            boolean assignmentAllowed,
                                            Set< ControlType > supportedControlTypes,
                                            boolean expired,
                                            Instant expireAt ) {
}
