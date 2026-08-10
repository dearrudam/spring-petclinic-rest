package org.springframework.samples.petclinic.visit.boundary.persistence.jpa;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.context.annotation.Profile;
import org.springframework.dao.DataAccessException;
import org.springframework.samples.petclinic.visit.control.VisitRepository;
import org.springframework.samples.petclinic.visit.entity.Visit;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
@Profile("jpa")
public class JpaVisitRepositoryImpl implements VisitRepository {

    @PersistenceContext
    private EntityManager em;

    @Override
    public void save(Visit visit) {
        if (visit.getId() == null) {
            this.em.persist(visit);
        } else {
            this.em.merge(visit);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Visit> findByPetId(Integer petId) {
        Query query = this.em.createQuery("SELECT v FROM Visit v where v.pet.id= :id");
        query.setParameter("id", petId);
        return query.getResultList();
    }

    @Override
    public Visit findById(int id) throws DataAccessException {
        return this.em.find(Visit.class, id);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Collection<Visit> findAll() throws DataAccessException {
        return this.em.createQuery("SELECT v FROM Visit v").getResultList();
    }

    @Override
    public void delete(Visit visit) throws DataAccessException {
        this.em.remove(this.em.contains(visit) ? visit : this.em.merge(visit));
    }
}
