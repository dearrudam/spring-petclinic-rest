/*
 * Copyright 2026 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.samples.petclinic.capabilities.pets;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/// Generated from the capability spec in this package. Do not edit.
/// Marks a test that realizes pet requirement statements.
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Requirement {

    /// One constant per statement in the capability specification.
    public enum Rn {
        R1_1("R1.1", "Quando a listagem for solicitada e houver pets cadastrados, a capacidade deverá fornecer todos os pets encontrados."),
        R1_2("R1.2", "Se a listagem for solicitada enquanto não houver pets cadastrados, então a capacidade deverá informar que nenhum pet foi encontrado."),
        R1_3("R1.3", "Quando a listagem paginada for solicitada, a capacidade deverá fornecer uma página de pets ordenados por identificador."),
        R2_1("R2.1", "Quando um pet existente for solicitado por seu identificador, a capacidade deverá fornecer os dados do pet, inclusive as visitas associadas."),
        R2_2("R2.2", "Se não existir pet com o identificador solicitado, então a capacidade deverá informar que o pet não foi encontrado."),
        R3_1("R3.1", "Quando novos nome, data de nascimento e tipo válidos forem informados para um pet existente, a capacidade deverá atualizar esses dados preservando a identidade do pet."),
        R3_2("R3.2", "Se não existir pet com o identificador informado para alteração, então a capacidade deverá informar que o pet não foi encontrado."),
        R3_3("R3.3", "Se o nome estiver ausente ou exceder 30 caracteres, se a data de nascimento estiver ausente, no futuro ou anterior a mais de 50 anos, ou se o tipo estiver ausente, então a capacidade deverá rejeitar a alteração."),
        R4_1("R4.1", "Quando a exclusão de um pet existente for solicitada, a capacidade deverá remover o pet."),
        R4_2("R4.2", "Se não existir pet com o identificador informado para exclusão, então a capacidade deverá informar que o pet não foi encontrado.");

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
