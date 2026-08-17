package org.springframework.samples.petclinic.capabilities.oops;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/// Generated from the capability spec in this package. Do not edit.
/// Marks the boundary method or test that realizes the given requirement statements.
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface OopsRequirement {

    enum Rn {
        /// Quando um erro de exemplo for solicitado, a capacidade deverá fornecer uma resposta de problema e não deverá fornecer uma resposta de sucesso.
        R1_1("R1.1", "Quando um erro de exemplo for solicitado, a capacidade deverá fornecer uma resposta de problema e não deverá fornecer uma resposta de sucesso.");

        private final String id;
        private final String statement;

        Rn(String id, String statement) {
            this.id = id;
            this.statement = statement;
        }

        public String statement() {
            return statement;
        }

        @Override
        public String toString() {
            return id;
        }
    }

    Rn[] value();
}
