package org.springframework.samples.petclinic.rest.validation;

import java.time.LocalDate;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.samples.petclinic.rest.dto.VisitDto;

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
