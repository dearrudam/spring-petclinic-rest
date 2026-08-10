package org.springframework.samples.petclinic.visit.boundary;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles({"spring-data-jpa", "hsqldb"})
class VisitFacadeSpringDataJpaTests extends AbstractVisitFacadeTests {
}
