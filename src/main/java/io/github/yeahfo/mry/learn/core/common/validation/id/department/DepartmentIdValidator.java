package io.github.yeahfo.mry.learn.core.common.validation.id.department;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

import static org.apache.commons.lang3.StringUtils.isBlank;

public class DepartmentIdValidator implements ConstraintValidator< DepartmentId, String > {

    private static final Pattern PATTERN = Pattern.compile( "^[0-9]{17,19}$" );

    @Override
    public boolean isValid( String departmentId, ConstraintValidatorContext context ) {
        if ( isBlank( departmentId ) ) {
            return true;
        }
        return isDepartmentId( departmentId );
    }

    public static boolean isDepartmentId( String departmentId ) {
        return PATTERN.matcher( departmentId ).matches( );
    }
}
