package io.github.yeahfo.mry.learn.common.domain;

import lombok.Getter;

@Getter
public enum Role {
    TENANT_ADMIN("系统管理员"),
    TENANT_MEMBER("普通成员"),
    ROBOT("API账号");

    private final String roleName;

    Role(String roleName) {
        this.roleName = roleName;
    }

}
