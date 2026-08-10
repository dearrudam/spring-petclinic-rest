package org.springframework.samples.petclinic.visit.control;

import org.springframework.dao.DataAccessException;
import org.springframework.samples.petclinic.model.BaseEntity;
import org.springframework.samples.petclinic.visit.entity.Visit;

import java.util.Collection;
import java.util.List;

public interface VisitRepository {

    /**
     * Save a visit to the data store, either inserting or updating it.
     *
     * @param visit the visit to save
     * @see BaseEntity#isNew
     */
    void save(Visit visit) throws DataAccessException;

    List<Visit> findByPetId(Integer petId);

    Visit findById(int id) throws DataAccessException;

    Collection<Visit> findAll() throws DataAccessException;

    void delete(Visit visit) throws DataAccessException;
}
