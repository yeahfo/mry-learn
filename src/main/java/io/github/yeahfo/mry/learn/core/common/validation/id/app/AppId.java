package io.github.yeahfo.mry.learn.core.common.validation.id.app;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE})
@Retention(RUNTIME)
@Constraint(validatedBy = AppIdValidator.class)
@Documented
public @interface AppId {
    String message( ) default "App ID format is incorrect.";

    Class<?>[] groups( ) default {};

    Class<? extends Payload>[] payload( ) default {};
}
