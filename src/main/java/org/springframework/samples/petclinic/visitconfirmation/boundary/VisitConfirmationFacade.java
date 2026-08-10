package org.springframework.samples.petclinic.visitconfirmation.boundary;

import org.springframework.samples.petclinic.visitconfirmation.control.VisitConfirmationControl;
import org.springframework.samples.petclinic.visitconfirmation.entity.VisitConfirmation;
import org.springframework.stereotype.Component;

@Component
public class VisitConfirmationFacade {

    private final VisitConfirmationControl control;

    public VisitConfirmationFacade(VisitConfirmationControl control) {
        this.control = control;
    }

    public VisitConfirmation confirmVisit(int visitId) {
        return control.confirmVisit(visitId);
    }
}
