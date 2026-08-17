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
package org.springframework.samples.petclinic.capabilities.owners;

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
public @interface OwnersRequirement {

    /// One constant per statement id in the spec's `## Requirements`.
    enum Rn {
        /// Quando um proprietário com todos os campos obrigatórios preenchidos e válidos for criado, a capacidade deverá persistir o proprietário e atribuir um identificador a ele.
        R1_1("R1.1", "Quando um proprietário com todos os campos obrigatórios preenchidos e válidos for criado, a capacidade deverá persistir o proprietário e atribuir um identificador a ele."),
        /// Se algum campo obrigatório estiver ausente, vazio ou fora das restrições de tamanho, então a capacidade deverá rejeitar a criação do proprietário.
        R1_2("R1.2", "Se algum campo obrigatório estiver ausente, vazio ou fora das restrições de tamanho, então a capacidade deverá rejeitar a criação do proprietário."),
        /// Se o primeiro nome ou o sobrenome não corresponder ao padrão de nome permitido, então a capacidade deverá rejeitar a criação do proprietário.
        R1_3("R1.3", "Se o primeiro nome ou o sobrenome não corresponder ao padrão de nome permitido, então a capacidade deverá rejeitar a criação do proprietário."),
        /// Se o telefone contiver caracteres não numéricos, então a capacidade deverá rejeitar a criação do proprietário.
        R1_4("R1.4", "Se o telefone contiver caracteres não numéricos, então a capacidade deverá rejeitar a criação do proprietário."),
        /// Quando a listagem de proprietários for solicitada sem filtro, a capacidade deverá fornecer todos os proprietários encontrados.
        R2_1("R2.1", "Quando a listagem de proprietários for solicitada sem filtro, a capacidade deverá fornecer todos os proprietários encontrados."),
        /// Quando a listagem de proprietários for solicitada com um sobrenome, a capacidade deverá fornecer apenas os proprietários cujo sobrenome corresponda ao termo informado.
        R2_2("R2.2", "Quando a listagem de proprietários for solicitada com um sobrenome, a capacidade deverá fornecer apenas os proprietários cujo sobrenome corresponda ao termo informado."),
        /// Se não houver proprietários correspondentes à solicitação, então a capacidade deverá informar que nenhum proprietário foi encontrado.
        R2_3("R2.3", "Se não houver proprietários correspondentes à solicitação, então a capacidade deverá informar que nenhum proprietário foi encontrado."),
        /// Quando uma página de proprietários com índice e tamanho válidos for solicitada, a capacidade deverá fornecer a página correspondente com o total de elementos e páginas.
        R3_1("R3.1", "Quando uma página de proprietários com índice e tamanho válidos for solicitada, a capacidade deverá fornecer a página correspondente com o total de elementos e páginas."),
        /// Se um índice de página negativo ou um tamanho fora do intervalo permitido for informado, então a capacidade deverá rejeitar a solicitação.
        R3_2("R3.2", "Se um índice de página negativo ou um tamanho fora do intervalo permitido for informado, então a capacidade deverá rejeitar a solicitação."),
        /// Quando um proprietário existente for solicitado por seu identificador, a capacidade deverá fornecer os dados do proprietário e os pets associados.
        R4_1("R4.1", "Quando um proprietário existente for solicitado por seu identificador, a capacidade deverá fornecer os dados do proprietário e os pets associados."),
        /// Se não existir proprietário com o identificador solicitado, então a capacidade deverá informar que o proprietário não foi encontrado.
        R4_2("R4.2", "Se não existir proprietário com o identificador solicitado, então a capacidade deverá informar que o proprietário não foi encontrado."),
        /// Quando novos valores válidos forem informados para um proprietário existente, a capacidade deverá atualizar os dados preservando a identidade.
        R5_1("R5.1", "Quando novos valores válidos forem informados para um proprietário existente, a capacidade deverá atualizar os dados preservando a identidade."),
        /// Se não existir proprietário com o identificador informado, então a capacidade deverá informar que o proprietário não foi encontrado.
        R5_2("R5.2", "Se não existir proprietário com o identificador informado, então a capacidade deverá informar que o proprietário não foi encontrado."),
        /// Se algum campo informado estiver ausente, vazio ou fora das restrições, então a capacidade deverá rejeitar a alteração.
        R5_3("R5.3", "Se algum campo informado estiver ausente, vazio ou fora das restrições, então a capacidade deverá rejeitar a alteração."),
        /// Quando a exclusão de um proprietário existente for solicitada, a capacidade deverá remover o proprietário.
        R6_1("R6.1", "Quando a exclusão de um proprietário existente for solicitada, a capacidade deverá remover o proprietário."),
        /// Se não existir proprietário com o identificador informado, então a capacidade deverá informar que o proprietário não foi encontrado.
        R6_2("R6.2", "Se não existir proprietário com o identificador informado, então a capacidade deverá informar que o proprietário não foi encontrado.");

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
