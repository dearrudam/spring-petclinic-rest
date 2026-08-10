package org.springframework.samples.petclinic.visit.boundary.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = VisitDateValidator.class)
@Documented
public @interface VisitDateValidation {

    String message() default "Visit date invalid! Visit date must be today or in the future.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
