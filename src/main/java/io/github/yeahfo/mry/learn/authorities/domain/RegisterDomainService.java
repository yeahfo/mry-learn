package io.github.yeahfo.mry.learn.authorities.domain;

import io.github.yeahfo.mry.learn.members.domain.MemberRepository;
import io.github.yeahfo.mry.learn.tenants.domain.TenantFactory;

public class RegisterDomainService {
    private final TenantFactory tenantFactory;
    private final MemberRepository memberRepository;

    public RegisterDomainService( TenantFactory tenantFactory, MemberRepository memberRepository ) {
        this.tenantFactory = tenantFactory;
        this.memberRepository = memberRepository;
    }
}
