package org.springframework.samples.petclinic.visit.boundary.persistence.springdatajpa;

import org.springframework.context.annotation.Profile;
import org.springframework.samples.petclinic.visit.entity.Visit;

@Profile("spring-data-jpa")
public interface VisitRepositoryOverride {

    void delete(Visit visit);
}
