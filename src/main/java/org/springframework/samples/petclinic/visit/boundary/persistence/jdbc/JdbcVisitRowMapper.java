package org.springframework.samples.petclinic.visit.boundary.persistence.jdbc;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.samples.petclinic.visit.entity.Visit;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

class JdbcVisitRowMapper implements RowMapper<Visit> {

    @Override
    public Visit mapRow(ResultSet rs, int row) throws SQLException {
        Visit visit = new Visit();
        visit.setId(rs.getInt("visit_id"));
        visit.setDate(rs.getObject("visit_date", LocalDate.class));
        visit.setDescription(rs.getString("description"));
        return visit;
    }
}
