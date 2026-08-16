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
package org.springframework.samples.petclinic.capabilities.visit;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/// Generated from the capability spec in this package. Do not edit.
/// Marks a test that realizes visit requirement statements.
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Requirement {

    /// One constant per statement in the capability specification.
    enum Rn {
        R1_1("R1.1", "Quando a listagem for solicitada e houver visitas registradas, a capacidade deverá fornecer todas as visitas encontradas."),
        R1_2("R1.2", "Se a listagem for solicitada enquanto não houver visitas registradas, então a capacidade deverá informar que nenhuma visita foi encontrada."),
        R2_1("R2.1", "Quando uma visita existente for solicitada por seu identificador, a capacidade deverá fornecer os dados da visita e o pet associado."),
        R2_2("R2.2", "Se não existir visita com o identificador solicitado, então a capacidade deverá informar que a visita não foi encontrada."),
        R3_1("R3.1", "Quando uma visita com pet identificado e descrição válida for registrada, a capacidade deverá persistir a visita associada ao pet e atribuir um identificador à visita."),
        R3_2("R3.2", "Se a descrição estiver ausente, vazia ou exceder 255 caracteres, então a capacidade deverá rejeitar o registro da visita."),
        R3_3("R3.3", "Quando uma visita for registrada sem data informada, a capacidade deverá atribuir a data atual à visita."),
        R3_4("R3.4", "Quando uma visita com data atual ou futura e demais dados válidos for registrada, a capacidade deverá aceitar o registro."),
        R3_5("R3.5", "Se uma visita for registrada com data anterior à data atual, então a capacidade deverá rejeitar o registro."),
        R4_1("R4.1", "Quando novos valores válidos de data e descrição forem informados para uma visita existente, a capacidade deverá atualizar esses dados preservando a identidade e o pet associado."),
        R4_2("R4.2", "Se não existir visita com o identificador informado para alteração, então a capacidade deverá informar que a visita não foi encontrada."),
        R4_3("R4.3", "Se a nova descrição estiver ausente, vazia ou exceder 255 caracteres, então a capacidade deverá rejeitar a alteração."),
        R5_1("R5.1", "Quando a exclusão de uma visita existente for solicitada, a capacidade deverá remover a visita."),
        R5_2("R5.2", "Se não existir visita com o identificador informado para exclusão, então a capacidade deverá informar que a visita não foi encontrada."),
        R6_1("R6.1", "Quando as visitas de um pet existente forem solicitadas, a capacidade deverá fornecer todas as visitas associadas ao pet, inclusive uma coleção vazia quando ele não possuir visitas.");

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
