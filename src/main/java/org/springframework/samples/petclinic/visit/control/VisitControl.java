package org.springframework.samples.petclinic.visit.control;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.orm.ObjectRetrievalFailureException;
import org.springframework.samples.petclinic.visit.entity.Visit;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;

@Service
public class VisitControl {

    private final VisitRepository visitRepository;

    public VisitControl(VisitRepository visitRepository) {
        this.visitRepository = visitRepository;
    }

    @Transactional(readOnly = true)
    public Collection<Visit> findAll() {
        return visitRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Visit findById(int visitId) {
        try {
            return visitRepository.findById(visitId);
        } catch (ObjectRetrievalFailureException | EmptyResultDataAccessException exception) {
            return null;
        }
    }

    @Transactional(readOnly = true)
    public Collection<Visit> findByPetId(int petId) {
        return visitRepository.findByPetId(petId);
    }

    @Transactional
    public void save(Visit visit) {
        visitRepository.save(visit);
    }

    @Transactional
    public void delete(Visit visit) {
        visitRepository.delete(visit);
    }
}
