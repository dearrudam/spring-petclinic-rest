package org.springframework.samples.petclinic.capabilities.root;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/// Gerado a partir da especificação da capacidade [root] — não editar.
/// Marca o método ou teste que realiza os requisitos declarados.
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RootRequirement {

    /// Um valor para cada requisito da seção {@code Requirements} da especificação.
    enum Rn {
        /// Quando a raiz da aplicação for acessada, a capacidade deverá redirecionar o cliente para a documentação interativa no contexto da aplicação.
        R1_1("R1.1", "Quando a raiz da aplicação for acessada, a capacidade deverá redirecionar o cliente para a documentação interativa no contexto da aplicação.");

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
