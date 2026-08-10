package org.springframework.samples.petclinic.visit.boundary.persistence.jdbc;

import org.springframework.data.jdbc.core.OneToManyResultSetExtractor;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.samples.petclinic.repository.jdbc.JdbcPet;
import org.springframework.samples.petclinic.repository.jdbc.JdbcPetRowMapper;
import org.springframework.samples.petclinic.visit.entity.Visit;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * {@link ResultSetExtractor} for the legacy owner JDBC adapter's pet and visit projection.
 */
public class JdbcPetVisitExtractor extends OneToManyResultSetExtractor<JdbcPet, Visit, Integer> {

    public JdbcPetVisitExtractor() {
        super(new JdbcPetRowMapper(), new JdbcVisitRowMapper());
    }

    @Override
    protected Integer mapPrimaryKey(ResultSet rs) throws SQLException {
        return rs.getInt("pets_id");
    }

    @Override
    protected Integer mapForeignKey(ResultSet rs) throws SQLException {
        if (rs.getObject("visits_pet_id") == null) {
            return null;
        }
        return rs.getInt("visits_pet_id");
    }

    @Override
    protected void addChild(JdbcPet root, Visit child) {
        root.addVisit(child);
    }
}
