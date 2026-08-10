package org.springframework.samples.petclinic.visit.boundary;

import org.junit.jupiter.api.Test;
import org.springframework.samples.petclinic.visit.control.VisitControl;
import org.springframework.samples.petclinic.visit.entity.Visit;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class VisitFacadeTests {

    @Test
    void delegatesAllBoundaryOperationsToVisitControl() {
        VisitControl visitControl = mock(VisitControl.class);
        VisitFacade visitFacade = new VisitFacade(visitControl);
        Visit visit = new Visit();
        List<Visit> visits = List.of(visit);
        given(visitControl.findAll()).willReturn(visits);
        given(visitControl.findById(1)).willReturn(visit);
        given(visitControl.findByPetId(2)).willReturn(visits);

        assertThat(visitFacade.listVisits()).isSameAs(visits);
        assertThat(visitFacade.getVisit(1)).isSameAs(visit);
        assertThat(visitFacade.listPetVisits(2)).isSameAs(visits);
        visitFacade.createVisit(visit);
        visitFacade.updateVisit(visit);
        visitFacade.deleteVisit(visit);

        verify(visitControl, times(2)).save(visit);
        verify(visitControl).delete(visit);
    }
}
