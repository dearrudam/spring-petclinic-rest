package org.springframework.samples.petclinic.visit.boundary;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@ActiveProfiles({"hsqldb", "jdbc"})
@TestPropertySource(properties = "spring.sql.init.platform=hsqldb")
class VisitFacadeHsqlJdbcTests extends AbstractVisitFacadeTests {
}
