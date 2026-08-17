package org.springframework.samples.petclinic.capabilities.specialties;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/// Gerado a partir da especificação da capacidade [specialties] — não editar.
/// Marca o método de boundary ou teste que realiza os requisitos declarados.
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface SpecialtiesRequirement {

    /// Um valor para cada requisito da seção `## Requirements` da especificação.
    enum Rn {
        /// Quando a listagem for solicitada e houver especialidades cadastradas, a capacidade deverá fornecer todas as especialidades encontradas.
        R1_1("R1.1", "Quando a listagem for solicitada e houver especialidades cadastradas, a capacidade deverá fornecer todas as especialidades encontradas."),
        /// Se a listagem for solicitada enquanto não houver especialidades cadastradas, então a capacidade deverá informar que nenhuma especialidade foi encontrada.
        R1_2("R1.2", "Se a listagem for solicitada enquanto não houver especialidades cadastradas, então a capacidade deverá informar que nenhuma especialidade foi encontrada."),
        /// Quando uma especialidade existente for solicitada por seu identificador, a capacidade deverá fornecer seus dados.
        R2_1("R2.1", "Quando uma especialidade existente for solicitada por seu identificador, a capacidade deverá fornecer seus dados."),
        /// Se não existir especialidade com o identificador solicitado, então a capacidade deverá informar que a especialidade não foi encontrada.
        R2_2("R2.2", "Se não existir especialidade com o identificador solicitado, então a capacidade deverá informar que a especialidade não foi encontrada."),
        /// Se um identificador negativo for informado para consulta, então a capacidade deverá rejeitar a solicitação.
        R2_3("R2.3", "Se um identificador negativo for informado para consulta, então a capacidade deverá rejeitar a solicitação."),
        /// Quando um nome válido for informado, a capacidade deverá persistir a especialidade e atribuir um identificador.
        R3_1("R3.1", "Quando um nome válido for informado, a capacidade deverá persistir a especialidade e atribuir um identificador."),
        /// Se o nome estiver ausente, vazio ou exceder 80 caracteres, então a capacidade deverá rejeitar o cadastro.
        R3_2("R3.2", "Se o nome estiver ausente, vazio ou exceder 80 caracteres, então a capacidade deverá rejeitar o cadastro."),
        /// Quando um nome válido for informado para uma especialidade existente, a capacidade deverá atualizar o nome preservando sua identidade.
        R4_1("R4.1", "Quando um nome válido for informado para uma especialidade existente, a capacidade deverá atualizar o nome preservando sua identidade."),
        /// Se não existir especialidade com o identificador informado, então a capacidade deverá informar que a especialidade não foi encontrada.
        R4_2("R4.2", "Se não existir especialidade com o identificador informado, então a capacidade deverá informar que a especialidade não foi encontrada."),
        /// Se o nome estiver ausente, vazio ou exceder 80 caracteres, então a capacidade deverá rejeitar a alteração.
        R4_3("R4.3", "Se o nome estiver ausente, vazio ou exceder 80 caracteres, então a capacidade deverá rejeitar a alteração."),
        /// Se um identificador negativo for informado para alteração, então a capacidade deverá rejeitar a solicitação.
        R4_4("R4.4", "Se um identificador negativo for informado para alteração, então a capacidade deverá rejeitar a solicitação."),
        /// Quando a exclusão de uma especialidade existente for solicitada, a capacidade deverá remover a especialidade.
        R5_1("R5.1", "Quando a exclusão de uma especialidade existente for solicitada, a capacidade deverá remover a especialidade."),
        /// Se não existir especialidade com o identificador informado, então a capacidade deverá informar que a especialidade não foi encontrada.
        R5_2("R5.2", "Se não existir especialidade com o identificador informado, então a capacidade deverá informar que a especialidade não foi encontrada."),
        /// Se um identificador negativo for informado para exclusão, então a capacidade deverá rejeitar a solicitação.
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
