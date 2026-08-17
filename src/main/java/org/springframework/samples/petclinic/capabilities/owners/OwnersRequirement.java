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
        /// Quando um proprietário com todos os campos obrigatórios preenchidos e válidos for criado, a capacidade deverá persistir o proprietário, atribuir um identificador e informar sua localização.
        R1_1("R1.1", "Quando um proprietário com todos os campos obrigatórios preenchidos e válidos for criado, a capacidade deverá persistir o proprietário, atribuir um identificador e informar sua localização."),
        /// Se nome, sobrenome, endereço, cidade ou telefone estiver ausente ou vazio, se nome ou sobrenome exceder 30 caracteres, se endereço exceder 255 caracteres, se cidade exceder 80 caracteres ou se telefone exceder 20 caracteres, então a capacidade deverá rejeitar a criação do proprietário.
        R1_2("R1.2", "Se nome, sobrenome, endereço, cidade ou telefone estiver ausente ou vazio, se nome ou sobrenome exceder 30 caracteres, se endereço exceder 255 caracteres, se cidade exceder 80 caracteres ou se telefone exceder 20 caracteres, então a capacidade deverá rejeitar a criação do proprietário."),
        /// Se o primeiro nome ou o sobrenome não corresponder ao padrão de nome permitido, então a capacidade deverá rejeitar a criação do proprietário.
        R1_3("R1.3", "Se o primeiro nome ou o sobrenome não corresponder ao padrão de nome permitido, então a capacidade deverá rejeitar a criação do proprietário."),
        /// Se o telefone contiver caracteres não numéricos, então a capacidade deverá rejeitar a criação do proprietário.
        R1_4("R1.4", "Se o telefone contiver caracteres não numéricos, então a capacidade deverá rejeitar a criação do proprietário."),
        /// Quando a listagem de proprietários for solicitada sem filtro, a capacidade deverá fornecer todos os proprietários encontrados.
        R2_1("R2.1", "Quando a listagem de proprietários for solicitada sem filtro, a capacidade deverá fornecer todos os proprietários encontrados."),
        /// Quando a listagem de proprietários for solicitada com um sobrenome, a capacidade deverá fornecer apenas os proprietários cujo sobrenome comece pelo termo informado.
        R2_2("R2.2", "Quando a listagem de proprietários for solicitada com um sobrenome, a capacidade deverá fornecer apenas os proprietários cujo sobrenome comece pelo termo informado."),
        /// Se não houver proprietários correspondentes à solicitação, então a capacidade deverá informar que nenhum proprietário foi encontrado.
        R2_3("R2.3", "Se não houver proprietários correspondentes à solicitação, então a capacidade deverá informar que nenhum proprietário foi encontrado."),
        /// Quando uma página de proprietários for solicitada, com índice e tamanho válidos ou omitidos e com sobrenome opcional, a capacidade deverá fornecer os proprietários correspondentes ordenados por identificador, o total de elementos e o total de páginas, usando índice zero e tamanho 20 quando omitidos.
        R3_1("R3.1", "Quando uma página de proprietários for solicitada, com índice e tamanho válidos ou omitidos e com sobrenome opcional, a capacidade deverá fornecer os proprietários correspondentes ordenados por identificador, o total de elementos e o total de páginas, usando índice zero e tamanho 20 quando omitidos."),
        /// Se um índice de página negativo ou um tamanho fora do intervalo permitido for informado, então a capacidade deverá rejeitar a solicitação.
        R3_2("R3.2", "Se um índice de página negativo ou um tamanho fora do intervalo permitido for informado, então a capacidade deverá rejeitar a solicitação."),
        /// Se não houver proprietários correspondentes à página solicitada, então a capacidade deverá fornecer uma página vazia.
        R3_3("R3.3", "Se não houver proprietários correspondentes à página solicitada, então a capacidade deverá fornecer uma página vazia."),
        /// Quando um proprietário existente for solicitado por seu identificador, a capacidade deverá fornecer os dados do proprietário e os pets associados.
        R4_1("R4.1", "Quando um proprietário existente for solicitado por seu identificador, a capacidade deverá fornecer os dados do proprietário e os pets associados."),
        /// Se não existir proprietário com o identificador solicitado, então a capacidade deverá informar que o proprietário não foi encontrado.
        R4_2("R4.2", "Se não existir proprietário com o identificador solicitado, então a capacidade deverá informar que o proprietário não foi encontrado."),
        /// Se um identificador negativo for informado para consulta, então a capacidade deverá rejeitar a solicitação.
        R4_3("R4.3", "Se um identificador negativo for informado para consulta, então a capacidade deverá rejeitar a solicitação."),
        /// Quando novos valores válidos forem informados para um proprietário existente, a capacidade deverá atualizar os dados preservando a identidade.
        R5_1("R5.1", "Quando novos valores válidos forem informados para um proprietário existente, a capacidade deverá atualizar os dados preservando a identidade."),
        /// Se não existir proprietário com o identificador informado, então a capacidade deverá informar que o proprietário não foi encontrado.
        R5_2("R5.2", "Se não existir proprietário com o identificador informado, então a capacidade deverá informar que o proprietário não foi encontrado."),
        /// Se nome, sobrenome, endereço, cidade ou telefone estiver ausente ou vazio, fora das restrições de tamanho ou de formato da criação, então a capacidade deverá rejeitar a alteração.
        R5_3("R5.3", "Se nome, sobrenome, endereço, cidade ou telefone estiver ausente ou vazio, fora das restrições de tamanho ou de formato da criação, então a capacidade deverá rejeitar a alteração."),
        /// Se um identificador negativo for informado para alteração, então a capacidade deverá rejeitar a solicitação.
        R5_4("R5.4", "Se um identificador negativo for informado para alteração, então a capacidade deverá rejeitar a solicitação."),
        /// Quando a exclusão de um proprietário existente for solicitada, a capacidade deverá remover o proprietário.
        R6_1("R6.1", "Quando a exclusão de um proprietário existente for solicitada, a capacidade deverá remover o proprietário."),
        /// Se não existir proprietário com o identificador informado, então a capacidade deverá informar que o proprietário não foi encontrado.
        R6_2("R6.2", "Se não existir proprietário com o identificador informado, então a capacidade deverá informar que o proprietário não foi encontrado."),
        /// Se um identificador negativo for informado para exclusão, então a capacidade deverá rejeitar a solicitação.
        R6_3("R6.3", "Se um identificador negativo for informado para exclusão, então a capacidade deverá rejeitar a solicitação."),
        /// Quando nome, data de nascimento e tipo válidos forem informados para um proprietário existente, a capacidade deverá persistir o pet associado ao proprietário, atribuir um identificador e informar a localização do pet.
        R7_1("R7.1", "Quando nome, data de nascimento e tipo válidos forem informados para um proprietário existente, a capacidade deverá persistir o pet associado ao proprietário, atribuir um identificador e informar a localização do pet."),
        /// Se não existir proprietário com o identificador informado, então a capacidade deverá informar que o proprietário não foi encontrado.
        R7_2("R7.2", "Se não existir proprietário com o identificador informado, então a capacidade deverá informar que o proprietário não foi encontrado."),
        /// Se o nome estiver ausente ou exceder 30 caracteres, se a data de nascimento estiver ausente, no futuro ou anterior a mais de 50 anos, ou se o tipo, seu identificador ou seu nome estiver ausente ou inválido, então a capacidade deverá rejeitar o cadastro do pet.
        R7_3("R7.3", "Se o nome estiver ausente ou exceder 30 caracteres, se a data de nascimento estiver ausente, no futuro ou anterior a mais de 50 anos, ou se o tipo, seu identificador ou seu nome estiver ausente ou inválido, então a capacidade deverá rejeitar o cadastro do pet."),
        /// Se um identificador negativo de proprietário for informado, então a capacidade deverá rejeitar a solicitação.
        R7_4("R7.4", "Se um identificador negativo de proprietário for informado, então a capacidade deverá rejeitar a solicitação."),
        /// Quando um pet associado a um proprietário existente for solicitado, a capacidade deverá fornecer os dados do pet e suas visitas.
        R8_1("R8.1", "Quando um pet associado a um proprietário existente for solicitado, a capacidade deverá fornecer os dados do pet e suas visitas."),
        /// Se o proprietário não existir ou não possuir um pet com o identificador informado, então a capacidade deverá informar que o proprietário ou o pet não foi encontrado.
        R8_2("R8.2", "Se o proprietário não existir ou não possuir um pet com o identificador informado, então a capacidade deverá informar que o proprietário ou o pet não foi encontrado."),
        /// Se um identificador negativo de proprietário ou pet for informado, então a capacidade deverá rejeitar a solicitação.
        R8_3("R8.3", "Se um identificador negativo de proprietário ou pet for informado, então a capacidade deverá rejeitar a solicitação."),
        /// Quando um proprietário e um pet existentes receberem nome, data de nascimento e tipo válidos, a capacidade deverá atualizar o pet preservando sua identidade.
        R9_1("R9.1", "Quando um proprietário e um pet existentes receberem nome, data de nascimento e tipo válidos, a capacidade deverá atualizar o pet preservando sua identidade."),
        /// Se o proprietário ou o pet não existir, então a capacidade deverá informar que o proprietário ou o pet não foi encontrado.
        R9_2("R9.2", "Se o proprietário ou o pet não existir, então a capacidade deverá informar que o proprietário ou o pet não foi encontrado."),
        /// Se os novos dados do pet violarem as restrições do cadastro, então a capacidade deverá rejeitar a alteração.
        R9_3("R9.3", "Se os novos dados do pet violarem as restrições do cadastro, então a capacidade deverá rejeitar a alteração."),
        /// Se um identificador negativo de proprietário ou pet for informado, então a capacidade deverá rejeitar a solicitação.
        R9_4("R9.4", "Se um identificador negativo de proprietário ou pet for informado, então a capacidade deverá rejeitar a solicitação."),
        /// Quando uma descrição válida e uma data atual ou futura forem informadas para um pet identificado, a capacidade deverá persistir a visita associada ao pet, atribuir um identificador e informar a localização da visita.
        R10_1("R10.1", "Quando uma descrição válida e uma data atual ou futura forem informadas para um pet identificado, a capacidade deverá persistir a visita associada ao pet, atribuir um identificador e informar a localização da visita."),
        /// Quando uma visita for registrada sem data, a capacidade deverá atribuir a data atual.
        R10_2("R10.2", "Quando uma visita for registrada sem data, a capacidade deverá atribuir a data atual."),
        /// Se a descrição estiver ausente, vazia ou exceder 255 caracteres, ou se a data for anterior à data atual, então a capacidade deverá rejeitar o registro da visita.
        R10_3("R10.3", "Se a descrição estiver ausente, vazia ou exceder 255 caracteres, ou se a data for anterior à data atual, então a capacidade deverá rejeitar o registro da visita."),
        /// Se um identificador negativo de proprietário ou pet for informado, então a capacidade deverá rejeitar a solicitação.
        R10_4("R10.4", "Se um identificador negativo de proprietário ou pet for informado, então a capacidade deverá rejeitar a solicitação."),
        /// Se o pet informado não puder ser associado à visita, então a capacidade deverá informar que o pet não foi encontrado sem expor detalhes técnicos da persistência.
        R10_5("R10.5", "Se o pet informado não puder ser associado à visita, então a capacidade deverá informar que o pet não foi encontrado sem expor detalhes técnicos da persistência.");

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
