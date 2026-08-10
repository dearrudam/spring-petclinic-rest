package org.springframework.samples.petclinic.visit.boundary.persistence.springdatajpa;

import org.springframework.context.annotation.Profile;
import org.springframework.data.repository.Repository;
import org.springframework.samples.petclinic.visit.control.VisitRepository;
import org.springframework.samples.petclinic.visit.entity.Visit;

@Profile("spring-data-jpa")
public interface SpringDataVisitRepository extends VisitRepository, Repository<Visit, Integer>, VisitRepositoryOverride {
}
