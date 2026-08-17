/// # Proprietários
/// > Gerenciar o cadastro e o ciclo de vida dos proprietários de pets.
///
/// ## Boundary
/// - `create-owner` — registrar um novo proprietário
/// - `list-owners` — listar os proprietários, opcionalmente filtrando por sobrenome
/// - `list-owners-page` — listar os proprietários de forma paginada, opcionalmente filtrando por sobrenome
/// - `get-owner` — consultar um proprietário por seu identificador
/// - `update-owner` — alterar os dados de um proprietário
/// - `delete-owner` — excluir um proprietário registrado
/// - `add-pet-to-owner` — cadastrar um pet para um proprietário
/// - `get-owner-pet` — consultar um pet no contexto de um proprietário
/// - `update-owner-pet` — alterar um pet pelo contexto de um proprietário
/// - `add-visit-to-owner-pet` — registrar uma visita para um pet pelo contexto de um proprietário
///
/// ## Requirements
/// ### R1: Criar proprietário
/// - R1.1 — Quando um proprietário com todos os campos obrigatórios preenchidos e válidos for criado, a capacidade deverá persistir o proprietário, atribuir um identificador e informar sua localização.
/// - R1.2 — Se nome, sobrenome, endereço, cidade ou telefone estiver ausente ou vazio, se nome ou sobrenome exceder 30 caracteres, se endereço exceder 255 caracteres, se cidade exceder 80 caracteres ou se telefone exceder 20 caracteres, então a capacidade deverá rejeitar a criação do proprietário.
/// - R1.3 — Se o primeiro nome ou o sobrenome não corresponder ao padrão de nome permitido, então a capacidade deverá rejeitar a criação do proprietário.
/// - R1.4 — Se o telefone contiver caracteres não numéricos, então a capacidade deverá rejeitar a criação do proprietário.
///
/// ### R2: Listar proprietários
/// - R2.1 — Quando a listagem de proprietários for solicitada sem filtro, a capacidade deverá fornecer todos os proprietários encontrados.
/// - R2.2 — Quando a listagem de proprietários for solicitada com um sobrenome, a capacidade deverá fornecer apenas os proprietários cujo sobrenome comece pelo termo informado.
/// - R2.3 — Se não houver proprietários correspondentes à solicitação, então a capacidade deverá informar que nenhum proprietário foi encontrado.
///
/// ### R3: Listar proprietários paginado
/// - R3.1 — Quando uma página de proprietários for solicitada, com índice e tamanho válidos ou omitidos e com sobrenome opcional, a capacidade deverá fornecer os proprietários correspondentes ordenados por identificador, o total de elementos e o total de páginas, usando índice zero e tamanho 20 quando omitidos.
/// - R3.2 — Se um índice de página negativo ou um tamanho fora do intervalo permitido for informado, então a capacidade deverá rejeitar a solicitação.
/// - R3.3 — Se não houver proprietários correspondentes à página solicitada, então a capacidade deverá fornecer uma página vazia.
///
/// ### R4: Consultar proprietário
/// - R4.1 — Quando um proprietário existente for solicitado por seu identificador, a capacidade deverá fornecer os dados do proprietário e os pets associados.
/// - R4.2 — Se não existir proprietário com o identificador solicitado, então a capacidade deverá informar que o proprietário não foi encontrado.
/// - R4.3 — Se um identificador negativo for informado para consulta, então a capacidade deverá rejeitar a solicitação.
///
/// ### R5: Alterar proprietário
/// - R5.1 — Quando novos valores válidos forem informados para um proprietário existente, a capacidade deverá atualizar os dados preservando a identidade.
/// - R5.2 — Se não existir proprietário com o identificador informado, então a capacidade deverá informar que o proprietário não foi encontrado.
/// - R5.3 — Se nome, sobrenome, endereço, cidade ou telefone estiver ausente ou vazio, fora das restrições de tamanho ou de formato da criação, então a capacidade deverá rejeitar a alteração.
/// - R5.4 — Se um identificador negativo for informado para alteração, então a capacidade deverá rejeitar a solicitação.
///
/// ### R6: Excluir proprietário
/// - R6.1 — Quando a exclusão de um proprietário existente for solicitada, a capacidade deverá remover o proprietário.
/// - R6.2 — Se não existir proprietário com o identificador informado, então a capacidade deverá informar que o proprietário não foi encontrado.
/// - R6.3 — Se um identificador negativo for informado para exclusão, então a capacidade deverá rejeitar a solicitação.
///
/// ### R7: Cadastrar pet para proprietário
/// - R7.1 — Quando nome, data de nascimento e tipo válidos forem informados para um proprietário existente, a capacidade deverá persistir o pet associado ao proprietário, atribuir um identificador e informar a localização do pet.
/// - R7.2 — Se não existir proprietário com o identificador informado, então a capacidade deverá informar que o proprietário não foi encontrado.
/// - R7.3 — Se o nome estiver ausente ou exceder 30 caracteres, se a data de nascimento estiver ausente, no futuro ou anterior a mais de 50 anos, ou se o tipo, seu identificador ou seu nome estiver ausente ou inválido, então a capacidade deverá rejeitar o cadastro do pet.
/// - R7.4 — Se um identificador negativo de proprietário for informado, então a capacidade deverá rejeitar a solicitação.
///
/// ### R8: Consultar pet do proprietário
/// - R8.1 — Quando um pet associado a um proprietário existente for solicitado, a capacidade deverá fornecer os dados do pet e suas visitas.
/// - R8.2 — Se o proprietário não existir ou não possuir um pet com o identificador informado, então a capacidade deverá informar que o proprietário ou o pet não foi encontrado.
/// - R8.3 — Se um identificador negativo de proprietário ou pet for informado, então a capacidade deverá rejeitar a solicitação.
///
/// ### R9: Alterar pet pelo contexto do proprietário
/// - R9.1 — Quando um proprietário e um pet existentes receberem nome, data de nascimento e tipo válidos, a capacidade deverá atualizar o pet preservando sua identidade.
/// - R9.2 — Se o proprietário ou o pet não existir, então a capacidade deverá informar que o proprietário ou o pet não foi encontrado.
/// - R9.3 — Se os novos dados do pet violarem as restrições do cadastro, então a capacidade deverá rejeitar a alteração.
/// - R9.4 — Se um identificador negativo de proprietário ou pet for informado, então a capacidade deverá rejeitar a solicitação.
///
/// ### R10: Registrar visita pelo contexto do proprietário
/// - R10.1 — Quando uma descrição válida e uma data atual ou futura forem informadas para um pet identificado, a capacidade deverá persistir a visita associada ao pet, atribuir um identificador e informar a localização da visita.
/// - R10.2 — Quando uma visita for registrada sem data, a capacidade deverá atribuir a data atual.
/// - R10.3 — Se a descrição estiver ausente, vazia ou exceder 255 caracteres, ou se a data for anterior à data atual, então a capacidade deverá rejeitar o registro da visita.
/// - R10.4 — Se um identificador negativo de proprietário ou pet for informado, então a capacidade deverá rejeitar a solicitação.
/// - R10.5 — Se o pet informado não puder ser associado à visita, então a capacidade deverá informar que o pet não foi encontrado sem expor detalhes técnicos da persistência.
///
/// ## Entities
/// - Owner
/// - Pet
/// - Visit
///
/// ## Out of scope
/// - Listar ou excluir pets e gerenciar pets fora do contexto de um proprietário; essas operações pertencem à capacidade de pets.
/// - Listar, consultar, alterar ou excluir visitas fora do contexto de um proprietário; essas operações pertencem à capacidade de visitas.
/// - Validar o vínculo entre o proprietário informado e o pet ao alterar o pet ou registrar uma visita.
/// - Definir o resultado da exclusão de um proprietário que possui pets enquanto os perfis de persistência apresentarem comportamentos distintos.
/// - Gerenciar veterinários, especialidades, tipos de pet ou usuários.
package org.springframework.samples.petclinic.capabilities.owners;
