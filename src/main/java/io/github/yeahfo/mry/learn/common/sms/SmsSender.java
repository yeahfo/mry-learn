package io.github.yeahfo.mry.learn.common.sms;

public interface SmsSender {
    boolean sendVerificationCode( String mobile, String code );
}
