package org.springframework.samples.petclinic.visit.boundary.persistence.springdatajpa;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.context.annotation.Profile;
import org.springframework.dao.DataAccessException;
import org.springframework.samples.petclinic.visit.entity.Visit;

@Profile("spring-data-jpa")
public class SpringDataVisitRepositoryImpl implements VisitRepositoryOverride {

    @PersistenceContext
    private EntityManager em;

    @Override
    public void delete(Visit visit) throws DataAccessException {
        String visitId = visit.getId().toString();
        this.em.createQuery("DELETE FROM Visit visit WHERE id=" + visitId).executeUpdate();
        if (em.contains(visit)) {
            em.remove(visit);
        }
    }
}
