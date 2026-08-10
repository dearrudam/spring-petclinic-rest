package org.springframework.samples.petclinic.visit.boundary;

import org.springframework.samples.petclinic.visit.control.VisitControl;
import org.springframework.samples.petclinic.visit.entity.Visit;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component
public class VisitFacade {

    private final VisitControl visitControl;

    public VisitFacade(VisitControl visitControl) {
        this.visitControl = visitControl;
    }

    public Collection<Visit> listVisits() {
        return visitControl.findAll();
    }

    public Visit getVisit(int visitId) {
        return visitControl.findById(visitId);
    }

    public Collection<Visit> listPetVisits(int petId) {
        return visitControl.findByPetId(petId);
    }

    public void createVisit(Visit visit) {
        visitControl.save(visit);
    }

    public void updateVisit(Visit visit) {
        visitControl.save(visit);
    }

    public void deleteVisit(Visit visit) {
        visitControl.delete(visit);
    }
}
