package org.springframework.samples.petclinic.visitconfirmation.boundary;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.samples.petclinic.rest.api.VisitConfirmationsApi;
import org.springframework.samples.petclinic.rest.dto.VisitConfirmationDto;
import org.springframework.samples.petclinic.visitconfirmation.control.VisitConfirmationControl.VisitNotEligibleException;
import org.springframework.samples.petclinic.visitconfirmation.control.VisitConfirmationControl.VisitNotFoundException;
import org.springframework.samples.petclinic.visitconfirmation.entity.VisitConfirmation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.ZoneOffset;

@RestController
@CrossOrigin(exposedHeaders = "errors, content-type")
@RequestMapping("api")
public class VisitConfirmationRestControllerV1 implements VisitConfirmationsApi {

    private final VisitConfirmationFacade facade;

    public VisitConfirmationRestControllerV1(VisitConfirmationFacade facade) {
        this.facade = facade;
    }

    @PreAuthorize("hasRole(@roles.OWNER_ADMIN)")
    @Override
    public ResponseEntity<VisitConfirmationDto> confirmVisit(Integer visitId) {
        try {
            VisitConfirmation confirmation = facade.confirmVisit(visitId);
            VisitConfirmationDto dto = new VisitConfirmationDto(
                confirmation.visitId(), confirmation.confirmedAt().atOffset(ZoneOffset.UTC));
            return ResponseEntity.ok(dto);
        } catch (VisitNotFoundException exception) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (VisitNotEligibleException exception) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
    }
}
