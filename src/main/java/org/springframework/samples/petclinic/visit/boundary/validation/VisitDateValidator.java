package org.springframework.samples.petclinic.visit.boundary.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.samples.petclinic.rest.dto.VisitDto;

import java.time.LocalDate;

public class VisitDateValidator implements ConstraintValidator<VisitDateValidation, VisitDto> {

    @Override
    public boolean isValid(VisitDto visit, ConstraintValidatorContext context) {
        if (visit == null || visit.getDate() == null || !visit.getDate().isBefore(LocalDate.now())) {
            return true;
        }

        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
            .addPropertyNode("date")
            .addConstraintViolation();
        return false;
    }
}
