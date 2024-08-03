package io.github.yeahfo.mry.learn.core.common.validation.id.tenant;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

import static org.apache.commons.lang3.StringUtils.isBlank;

public class TenantIdValidator implements ConstraintValidator<TenantId, String> {

    private static final Pattern PATTERN = Pattern.compile("^[0-9]{17,19}$");

    @Override
    public boolean isValid(String tenantId, ConstraintValidatorContext context) {
        if (isBlank(tenantId)) {
            return true;
        }

        return PATTERN.matcher(tenantId).matches();
    }

}
