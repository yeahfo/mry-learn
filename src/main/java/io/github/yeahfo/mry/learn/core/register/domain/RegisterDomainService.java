package io.github.yeahfo.mry.learn.core.register.domain;

import io.github.yeahfo.mry.learn.core.common.domain.User;
import io.github.yeahfo.mry.learn.core.common.exception.MryException;
import io.github.yeahfo.mry.learn.core.common.utils.CommonUtils;
import io.github.yeahfo.mry.learn.core.member.domain.MemberRepository;
import io.github.yeahfo.mry.learn.core.tenant.domain.TenantFactory;
import io.github.yeahfo.mry.learn.core.tenant.domain.TenantMadeHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static io.github.yeahfo.mry.learn.core.common.exception.ErrorCode.MEMBER_WITH_MOBILE_OR_EMAIL_ALREADY_EXISTS;
import static io.github.yeahfo.mry.learn.core.common.utils.CommonUtils.*;

@Component
public class RegisterDomainService {
    private final MemberRepository memberRepository;

    public RegisterDomainService( MemberRepository memberRepository ) {
        this.memberRepository = memberRepository;
    }

    public TenantMadeHolder register( String mobileOrEmail, String password, String tenantName, User user ) {
        if ( memberRepository.existsByMobileOrEmail( mobileOrEmail ) ) {
            throw new MryException( MEMBER_WITH_MOBILE_OR_EMAIL_ALREADY_EXISTS, "注册失败，手机号或邮箱已被占用。",
                    "mobileOrEmail", maskMobileOrEmail( mobileOrEmail ) );
        }
        Optional< String > selector = Optional.of( mobileOrEmail );
        return TenantFactory.make( tenantName,
                selector.filter( CommonUtils::isMobileNumber ).orElse( null ),
                selector.filter( CommonUtils::isEmail ).orElse( null ),
                password, user );
    }
}
