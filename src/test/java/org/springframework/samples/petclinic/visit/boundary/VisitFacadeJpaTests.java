package org.springframework.samples.petclinic.visit.boundary;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles({"jpa", "hsqldb"})
class VisitFacadeJpaTests extends AbstractVisitFacadeTests {
}
