package io.github.yeahfo.mry.learn.core.verification.infrastructure;

import io.github.yeahfo.mry.learn.common.email.EmailSender;
import io.github.yeahfo.mry.learn.common.sms.SmsSender;
import io.github.yeahfo.mry.learn.core.tenant.domain.task.TenantSmsUsageCountTask;
import io.github.yeahfo.mry.learn.core.verification.domain.VerificationCode;
import io.github.yeahfo.mry.learn.core.verification.domain.VerificationCodeSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Component;

import static io.github.yeahfo.mry.learn.core.common.utils.CommonUtils.isMobileNumber;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Slf4j
@Component
@Profile( "!prod" )
@RequiredArgsConstructor
public class DefaultVerificationCodeSender implements VerificationCodeSender {
    private final SmsSender smsSender;
    private final EmailSender emailSender;
    private final TaskExecutor taskExecutor;
    private final TenantSmsUsageCountTask tenantSmsUsageCountTask;

    @Override
    public void send( VerificationCode code ) {
        String mobileEmail = code.mobileEmail( );
        String payload = code.code( );
        if ( isMobileNumber( mobileEmail ) ) {
            taskExecutor.execute( ( ) -> {
                boolean result = smsSender.sendVerificationCode( mobileEmail, payload );
                if ( result && isNotBlank( code.tenantId( ) ) ) {
                    tenantSmsUsageCountTask.run( code.tenantId( ) );
                }
            } );
        } else {
            taskExecutor.execute( ( ) -> emailSender.sendVerificationCode( mobileEmail, payload ) );
        }
    }
}
