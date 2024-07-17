package io.github.yeahfo.mry.learn.core.verification.domain;

public interface VerificationCodeSender {
    void send( VerificationCode code );
}
