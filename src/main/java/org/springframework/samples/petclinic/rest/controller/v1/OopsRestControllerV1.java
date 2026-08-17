package org.springframework.samples.petclinic.rest.controller.v1;

import java.net.URI;
import java.time.Instant;
import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.samples.petclinic.capabilities.oops.OopsRequirement;
import org.springframework.samples.petclinic.rest.api.OopsApi;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.samples.petclinic.capabilities.oops.OopsRequirement.Rn.R1_1;

@RestController
@CrossOrigin(exposedHeaders = "errors, content-type")
@RequestMapping("api")
public class OopsRestControllerV1 implements OopsApi {

    @Override
    @OopsRequirement(R1_1)
    public ResponseEntity<Void> failingRequest() {
        throw new SampleErrorException();
    }

    @ExceptionHandler(SampleErrorException.class)
    private ResponseEntity<ProblemDetail> sampleError(HttpServletRequest request) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "A sample error was requested.");
        problem.setType(URI.create(request.getRequestURL().toString()));
        problem.setTitle("Sample error");
        problem.setProperty("timestamp", Instant.now());
        problem.setProperty("schemaValidationErrors", List.of());
        return ResponseEntity.badRequest().body(problem);
    }

    private static final class SampleErrorException extends RuntimeException {
    }
}
