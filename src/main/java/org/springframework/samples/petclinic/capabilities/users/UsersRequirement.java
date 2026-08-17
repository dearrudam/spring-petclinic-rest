package org.springframework.samples.petclinic.capabilities.users;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/// Gerado a partir da especificação da capacidade [users] — não editar.
/// Marca o método ou teste que realiza os requisitos declarados.
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface UsersRequirement {

    /// Um valor para cada requisito da seção {@code Requirements} da especificação.
    enum Rn {
        /// Quando um usuário com nome e ao menos um papel de acesso válidos for cadastrado, a capacidade deverá persistir o usuário e fornecer seus dados.
        R1_1("R1.1", "Quando um usuário com nome e ao menos um papel de acesso válidos for cadastrado, a capacidade deverá persistir o usuário e fornecer seus dados."),
        /// Se o nome do usuário estiver ausente, vazio ou exceder 80 caracteres, se a senha informada estiver vazia ou exceder 80 caracteres, ou se o nome de um papel estiver vazio ou exceder 80 caracteres, então a capacidade deverá rejeitar o cadastro.
        R1_2("R1.2", "Se o nome do usuário estiver ausente, vazio ou exceder 80 caracteres, se a senha informada estiver vazia ou exceder 80 caracteres, ou se o nome de um papel estiver vazio ou exceder 80 caracteres, então a capacidade deverá rejeitar o cadastro."),
        /// Se nenhum papel de acesso for informado, então a capacidade deverá rejeitar o cadastro.
        R1_3("R1.3", "Se nenhum papel de acesso for informado, então a capacidade deverá rejeitar o cadastro."),
        /// Quando um papel sem o prefixo ROLE_ for informado, a capacidade deverá adicionar o prefixo antes de persistir o usuário.
        R1_4("R1.4", "Quando um papel sem o prefixo `ROLE_` for informado, a capacidade deverá adicionar o prefixo antes de persistir o usuário.");

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
