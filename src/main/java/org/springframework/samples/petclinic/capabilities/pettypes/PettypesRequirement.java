package org.springframework.samples.petclinic.capabilities.pettypes;

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
public @interface PettypesRequirement {

    enum Rn {
        R1_1("R1.1", "Quando a listagem for solicitada e houver tipos de pet cadastrados, a capacidade deverá fornecer todos os tipos encontrados."),
        R1_2("R1.2", "Se a listagem for solicitada enquanto não houver tipos de pet cadastrados, então a capacidade deverá informar que nenhum tipo de pet foi encontrado."),
        R2_1("R2.1", "Quando um tipo de pet existente for solicitado por seu identificador, a capacidade deverá fornecer seus dados."),
        R2_2("R2.2", "Se não existir tipo de pet com o identificador solicitado, então a capacidade deverá informar que o tipo de pet não foi encontrado."),
        R2_3("R2.3", "Se um identificador negativo for informado para consulta, então a capacidade deverá rejeitar a solicitação."),
        R3_1("R3.1", "Quando um nome válido for informado, a capacidade deverá persistir o tipo de pet, atribuir um identificador e informar sua localização."),
        R3_2("R3.2", "Se o nome estiver ausente, vazio ou exceder 80 caracteres, então a capacidade deverá rejeitar o cadastro."),
        R4_1("R4.1", "Quando um nome válido for informado para um tipo de pet existente, a capacidade deverá atualizar o nome preservando sua identidade."),
        R4_2("R4.2", "Se não existir tipo de pet com o identificador informado, então a capacidade deverá informar que o tipo de pet não foi encontrado."),
        R4_3("R4.3", "Se o nome estiver ausente, vazio ou exceder 80 caracteres, então a capacidade deverá rejeitar a alteração."),
        R4_4("R4.4", "Se um identificador negativo for informado para alteração, então a capacidade deverá rejeitar a solicitação."),
        R5_1("R5.1", "Quando a exclusão de um tipo de pet existente for solicitada, a capacidade deverá remover o tipo de pet."),
        R5_2("R5.2", "Se não existir tipo de pet com o identificador informado, então a capacidade deverá informar que o tipo de pet não foi encontrado."),
        R5_3("R5.3", "Se um identificador negativo for informado para exclusão, então a capacidade deverá rejeitar a solicitação.");

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
