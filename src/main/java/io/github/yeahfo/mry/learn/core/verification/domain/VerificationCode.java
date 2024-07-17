package io.github.yeahfo.mry.learn.core.verification.domain;

import io.github.yeahfo.mry.learn.core.common.domain.AggregateRoot;
import io.github.yeahfo.mry.learn.core.common.domain.User;
import io.github.yeahfo.mry.learn.core.common.exception.MryException;

import static io.github.yeahfo.mry.learn.core.common.exception.ErrorCode.VERIFICATION_CODE_COUNT_OVERFLOW;
import static io.github.yeahfo.mry.learn.core.common.utils.MryConstants.NO_TENANT_ID;
import static io.github.yeahfo.mry.learn.core.common.utils.SnowflakeIdGenerator.newSnowflakeIdAsString;
import static org.apache.commons.lang3.RandomStringUtils.randomNumeric;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

public class VerificationCode extends AggregateRoot {
    private String mobileEmail;//邮箱或手机号
    private String code;//6位数验证码
    private VerificationCodeType type;//验证码用于的类型
    private int usedCount;//已经使用的次数，使用次数不能超过3次

    public VerificationCode( String mobileEmail, VerificationCodeType type, String tenantId, User user ) {
        super( newVerificationCodeId( ), isNotBlank( tenantId ) ? tenantId : NO_TENANT_ID, user );
        this.mobileEmail = mobileEmail;
        this.code = randomNumeric( 6 );
        this.type = type;
        this.usedCount = 0;
    }

    public String mobileEmail( ) {
        return mobileEmail;
    }

    public String code( ) {
        return code;
    }

    public static String newVerificationCodeId( ) {
        return newSnowflakeIdAsString( );
    }

    public void use( ) {
        if ( usedCount >= 3 ) {
            throw new MryException( VERIFICATION_CODE_COUNT_OVERFLOW, "验证码已超过可使用次数。" );
        }
        usedCount++;
    }
}
