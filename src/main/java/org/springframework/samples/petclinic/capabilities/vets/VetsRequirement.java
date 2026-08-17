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
package org.springframework.samples.petclinic.capabilities.vets;

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
public @interface VetsRequirement {

    /// One constant per statement id in the spec's `## Requirements`.
    enum Rn {
        /// Quando a listagem for solicitada e houver veterinarios cadastrados, a capacidade devera fornecer todos os veterinarios encontrados.
        R1_1("R1.1", "Quando a listagem for solicitada e houver veterinarios cadastrados, a capacidade devera fornecer todos os veterinarios encontrados."),
        /// Se a listagem for solicitada enquanto nao houver veterinarios cadastrados, entao a capacidade devera informar que nenhum veterinario foi encontrado.
        R1_2("R1.2", "Se a listagem for solicitada enquanto nao houver veterinarios cadastrados, entao a capacidade devera informar que nenhum veterinario foi encontrado."),
        /// Quando um veterinario existente for solicitado por seu identificador, a capacidade devera fornecer seus dados e especialidades.
        R2_1("R2.1", "Quando um veterinario existente for solicitado por seu identificador, a capacidade devera fornecer seus dados e especialidades."),
        /// Se nao existir veterinario com o identificador solicitado, entao a capacidade devera informar que o veterinario nao foi encontrado.
        R2_2("R2.2", "Se nao existir veterinario com o identificador solicitado, entao a capacidade devera informar que o veterinario nao foi encontrado."),
        /// Quando um veterinario com nome, sobrenome e especialidades validos for cadastrado, a capacidade devera persistir o veterinario, atribuir-lhe um identificador e informar sua localizacao.
        R3_1("R3.1", "Quando um veterinario com nome, sobrenome e especialidades validos for cadastrado, a capacidade devera persistir o veterinario, atribuir-lhe um identificador e informar sua localizacao."),
        /// Se o nome ou o sobrenome estiver ausente, vazio, fora do formato ou fora do limite permitido, ou se a colecao de especialidades estiver ausente, entao a capacidade devera rejeitar o cadastro.
        R3_2("R3.2", "Se o nome ou o sobrenome estiver ausente, vazio, fora do formato ou fora do limite permitido, ou se a colecao de especialidades estiver ausente, entao a capacidade devera rejeitar o cadastro."),
        /// Quando especialidades forem informadas no cadastro, a capacidade devera associar ao veterinario as especialidades cadastradas com os nomes informados.
        R3_3("R3.3", "Quando especialidades forem informadas no cadastro, a capacidade devera associar ao veterinario as especialidades cadastradas com os nomes informados."),
        /// Quando novos nome, sobrenome e especialidades validos forem informados para um veterinario existente, a capacidade devera atualizar esses dados preservando sua identidade.
        R4_1("R4.1", "Quando novos nome, sobrenome e especialidades validos forem informados para um veterinario existente, a capacidade devera atualizar esses dados preservando sua identidade."),
        /// Se nao existir veterinario com o identificador informado para alteracao, entao a capacidade devera informar que o veterinario nao foi encontrado.
        R4_2("R4.2", "Se nao existir veterinario com o identificador informado para alteracao, entao a capacidade devera informar que o veterinario nao foi encontrado."),
        /// Se o nome ou o sobrenome da alteracao estiver ausente, vazio, fora do formato ou fora do limite permitido, ou se a colecao de especialidades estiver ausente, entao a capacidade devera rejeitar a alteracao.
        R4_3("R4.3", "Se o nome ou o sobrenome da alteracao estiver ausente, vazio, fora do formato ou fora do limite permitido, ou se a colecao de especialidades estiver ausente, entao a capacidade devera rejeitar a alteracao."),
        /// Quando a exclusao de um veterinario existente for solicitada, a capacidade devera remover o veterinario.
        R5_1("R5.1", "Quando a exclusao de um veterinario existente for solicitada, a capacidade devera remover o veterinario."),
        /// Se nao existir veterinario com o identificador informado para exclusao, entao a capacidade devera informar que o veterinario nao foi encontrado.
        R5_2("R5.2", "Se nao existir veterinario com o identificador informado para exclusao, entao a capacidade devera informar que o veterinario nao foi encontrado.");

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
