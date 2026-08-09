package org.springframework.samples.petclinic.rest.validation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = VisitDateValidator.class)
@Documented
public @interface VisitDateValidation {

    String message() default "Visit date must be today or in the future.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
