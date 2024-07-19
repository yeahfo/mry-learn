package io.github.yeahfo.mry.learn.common.sms;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsRequest;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import static com.aliyuncs.http.MethodType.POST;
import static io.github.yeahfo.mry.learn.core.common.utils.CommonUtils.maskMobileOrEmail;

@Slf4j
@Component
@Profile( "!prod" )
@RequiredArgsConstructor
public class AliYunSmsSender implements SmsSender {
    @Value( "${spring.messages.ali-yun.access-key}" )
    private String accessKey;
    @Value( "${spring.messages.ali-yun.secret}" )
    private String secret;
    @Value( "${spring.messages.ali-yun.sms-sign-name}" )
    private String signName;
    @Value( "${spring.messages.ali-yun.sms-template-code}" )
    private String templateCode;
    private IAcsClient acsClient;

    @Override
    public boolean sendVerificationCode( String mobile, String code ) {
        SendSmsRequest request = new SendSmsRequest( );
        request.setSysMethod( POST );
        request.setPhoneNumbers( mobile );
        request.setSignName( signName );
        request.setTemplateCode( templateCode );
        request.setTemplateParam( "{\"code\":\"" + code + "\"}" );
        try {
            SendSmsResponse response = acsClient( ).getAcsResponse( request );
            if ( "OK".equalsIgnoreCase( response.getCode( ) ) ) {
                log.info( "Sent SMS verification code to [{}].", maskMobileOrEmail( mobile ) );
                return true;
            } else {
                log.error( "Failed to send verification code to mobile[{}]: {}.", maskMobileOrEmail( mobile ), response.getMessage( ) );
                return false;
            }
        } catch ( Throwable t ) {
            log.error( "Failed to send verification code to mobile[{}].", maskMobileOrEmail( mobile ), t );
            return false;
        }
    }

    private IAcsClient acsClient( ) {
        if ( acsClient == null ) {
            IClientProfile profile = DefaultProfile.getProfile( "cn-guangzhou", accessKey, secret );
            DefaultProfile.addEndpoint( "cn-guangzhou", "Dysmsapi", "dysmsapi.aliyuncs.com" );
            acsClient = new DefaultAcsClient( profile );
        }
        return acsClient;
    }
}
