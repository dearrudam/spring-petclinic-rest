package org.springframework.samples.petclinic.visit.boundary;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.samples.petclinic.model.Pet;
import org.springframework.samples.petclinic.util.EntityUtils;
import org.springframework.samples.petclinic.visit.entity.Visit;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;

abstract class AbstractVisitFacadeTests {

    @Autowired
    protected VisitFacade visitFacade;

    @Test
    @DisplayName("R1.5 provides visits associated with a pet")
    void shouldFindVisitsByPetId() {
        Collection<Visit> visits = visitFacade.listPetVisits(7);

        assertThat(visits).hasSize(2)
            .allSatisfy(visit -> {
                assertThat(visit.getPet()).isNotNull();
                assertThat(visit.getDate()).isNotNull();
                assertThat(visit.getPet().getId()).isEqualTo(7);
            });
    }

    @Test
    void shouldFindVisitById() {
        Visit visit = visitFacade.getVisit(1);

        assertThat(visit.getId()).isEqualTo(1);
        assertThat(visit.getPet().getName()).isEqualTo("Samantha");
    }

    @Test
    void shouldFindAllVisits() {
        Collection<Visit> visits = visitFacade.listVisits();

        assertThat(EntityUtils.getById(visits, Visit.class, 1).getPet().getName()).isEqualTo("Samantha");
        assertThat(EntityUtils.getById(visits, Visit.class, 3).getPet().getName()).isEqualTo("Max");
    }

    @Test
    @DisplayName("R2.1 persists a new visit and assigns its identity")
    @Transactional
    void shouldInsertVisit() {
        int originalCount = visitFacade.listVisits().size();
        Pet pet = visitFacade.getVisit(1).getPet();
        Visit visit = new Visit();
        visit.setPet(pet);
        visit.setDate(LocalDate.now());
        visit.setDescription("new visit");

        visitFacade.createVisit(visit);

        assertThat(visit.getId()).isPositive();
        assertThat(visitFacade.listVisits()).hasSize(originalCount + 1);
    }

    @Test
    @DisplayName("R3.1 persists changes to an existing visit")
    @Transactional
    void shouldUpdateVisit() {
        Visit visit = visitFacade.getVisit(1);
        Integer visitId = visit.getId();
        Pet pet = visit.getPet();
        String newDescription = visit.getDescription() + "X";
        LocalDate newDate = visit.getDate().plusDays(1);
        visit.setDescription(newDescription);
        visit.setDate(newDate);

        visitFacade.updateVisit(visit);
        Visit updatedVisit = visitFacade.getVisit(1);

        assertThat(updatedVisit.getId()).isEqualTo(visitId);
        assertThat(updatedVisit.getPet().getId()).isEqualTo(pet.getId());
        assertThat(updatedVisit.getDate()).isEqualTo(newDate);
        assertThat(updatedVisit.getDescription()).isEqualTo(newDescription);
    }

    @Test
    @DisplayName("R4.1 removes an existing visit from persistence")
    @Transactional
    void shouldDeleteVisit() {
        Visit visit = visitFacade.getVisit(1);

        visitFacade.deleteVisit(visit);

        assertThat(visitFacade.getVisit(1)).isNull();
    }
}
