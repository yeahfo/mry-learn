package io.github.yeahfo.mry.learn.common.email;

import io.github.yeahfo.mry.learn.core.common.domain.UploadedFile;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import java.util.List;

import static io.github.yeahfo.mry.learn.core.common.utils.CommonUtils.maskMobileOrEmail;

@Slf4j
@Component
@Profile( "!prod" )
@RequiredArgsConstructor
public class DefaultEmailSender implements EmailSender {
    private final JavaMailSender mailSender;
    @Value( "${spring.mail.username}" )
    private String from;

    @Override
    public void sendVerificationCode( String email, String code ) {
        SimpleMailMessage message = new SimpleMailMessage( );
        message.setTo( email );
        message.setFrom( String.join( "", "<", from, ">" ) );
        message.setSubject( "Verification Code" );
        message.setText( "Your verification code is: " + code + ". This verification code is valid within 10 minutes. Please do not disclose it to others." );
        mailSender.send( message );
        log.info( "Sent email verification code to [{}].", maskMobileOrEmail( email ) );
    }

    @Override
    public void notifyWebhookNotAccessible( List< String > emails, String appName, String webhookUrl ) {

    }

    @Override
    public void sendInvoice( String email, List< UploadedFile > invoiceFiles ) {

    }
}
