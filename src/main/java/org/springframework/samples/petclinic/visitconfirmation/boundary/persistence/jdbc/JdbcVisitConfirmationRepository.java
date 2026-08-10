package org.springframework.samples.petclinic.visitconfirmation.boundary.persistence.jdbc;

import org.springframework.boot.sql.init.dependency.DependsOnDatabaseInitialization;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.samples.petclinic.visitconfirmation.control.VisitConfirmationRepository;
import org.springframework.samples.petclinic.visitconfirmation.entity.VisitConfirmation;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;

@Repository
@DependsOnDatabaseInitialization
public class JdbcVisitConfirmationRepository implements VisitConfirmationRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public JdbcVisitConfirmationRepository(DataSource dataSource) {
        this.jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }

    @Override
    public Optional<LocalDate> lockVisitDate(int visitId) {
        return jdbcTemplate.query(
            "SELECT visit_date FROM visits WHERE id=:visitId FOR UPDATE",
            Map.of("visitId", visitId),
            (resultSet, rowNumber) -> resultSet.getDate("visit_date").toLocalDate())
            .stream().findFirst();
    }

    @Override
    public Optional<VisitConfirmation> findByVisitId(int visitId) {
        return jdbcTemplate.query(
            "SELECT visit_id, confirmed_at FROM visit_confirmations WHERE visit_id=:visitId",
            Map.of("visitId", visitId),
            (resultSet, rowNumber) -> new VisitConfirmation(
                resultSet.getInt("visit_id"), resultSet.getTimestamp("confirmed_at").toInstant()))
            .stream().findFirst();
    }

    @Override
    public void save(VisitConfirmation confirmation) {
        jdbcTemplate.update(
            "INSERT INTO visit_confirmations (visit_id, confirmed_at) VALUES (:visitId, :confirmedAt)",
            new MapSqlParameterSource()
                .addValue("visitId", confirmation.visitId())
                .addValue("confirmedAt", Timestamp.from(confirmation.confirmedAt())));
    }
}
