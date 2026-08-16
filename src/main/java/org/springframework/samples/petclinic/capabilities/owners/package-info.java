/// # Proprietários
/// > Gerenciar o cadastro e o ciclo de vida dos proprietários de pets.
///
/// ## Boundary
/// - `create-owner` — registrar um novo proprietário
/// - `list-owners` — listar os proprietários, opcionalmente filtrando por sobrenome
/// - `list-owners-page` — listar os proprietários de forma paginada
/// - `get-owner` — consultar um proprietário por seu identificador
/// - `update-owner` — alterar os dados de um proprietário
/// - `delete-owner` — excluir um proprietário registrado
///
/// ## Requirements
/// ### R1: Criar proprietário
/// - R1.1 — Quando um proprietário com todos os campos obrigatórios preenchidos e válidos for criado, a capacidade deverá persistir o proprietário e atribuir um identificador a ele.
/// - R1.2 — Se algum campo obrigatório estiver ausente, vazio ou fora das restrições de tamanho, então a capacidade deverá rejeitar a criação do proprietário.
/// - R1.3 — Se o primeiro nome ou o sobrenome não corresponder ao padrão de nome permitido, então a capacidade deverá rejeitar a criação do proprietário.
/// - R1.4 — Se o telefone contiver caracteres não numéricos, então a capacidade deverá rejeitar a criação do proprietário.
///
/// ### R2: Listar proprietários
/// - R2.1 — Quando a listagem de proprietários for solicitada sem filtro, a capacidade deverá fornecer todos os proprietários encontrados.
/// - R2.2 — Quando a listagem de proprietários for solicitada com um sobrenome, a capacidade deverá fornecer apenas os proprietários cujo sobrenome corresponda ao termo informado.
/// - R2.3 — Se não houver proprietários correspondentes à solicitação, então a capacidade deverá informar que nenhum proprietário foi encontrado.
///
/// ### R3: Listar proprietários paginado
/// - R3.1 — Quando uma página de proprietários com índice e tamanho válidos for solicitada, a capacidade deverá fornecer a página correspondente com o total de elementos e páginas.
/// - R3.2 — Se um índice de página negativo ou um tamanho fora do intervalo permitido for informado, então a capacidade deverá rejeitar a solicitação.
///
/// ### R4: Consultar proprietário
/// - R4.1 — Quando um proprietário existente for solicitado por seu identificador, a capacidade deverá fornecer os dados do proprietário e os pets associados.
/// - R4.2 — Se não existir proprietário com o identificador solicitado, então a capacidade deverá informar que o proprietário não foi encontrado.
///
/// ### R5: Alterar proprietário
/// - R5.1 — Quando novos valores válidos forem informados para um proprietário existente, a capacidade deverá atualizar os dados preservando a identidade.
/// - R5.2 — Se não existir proprietário com o identificador informado, então a capacidade deverá informar que o proprietário não foi encontrado.
/// - R5.3 — Se algum campo informado estiver ausente, vazio ou fora das restrições, então a capacidade deverá rejeitar a alteração.
///
/// ### R6: Excluir proprietário
/// - R6.1 — Quando a exclusão de um proprietário existente for solicitada, a capacidade deverá remover o proprietário.
/// - R6.2 — Se não existir proprietário com o identificador informado, então a capacidade deverá informar que o proprietário não foi encontrado.
///
/// ## Entities
/// - Owner
///
/// ## Out of scope
/// - Gerenciar pets e visitas vinculados a um proprietário; essas capacidades pertencem às capacidades de pets e visitas.
/// - Definir o resultado da exclusão de um proprietário que possui pets enquanto os perfis de persistência apresentarem comportamentos distintos.
/// - Gerenciar veterinários, especialidades, tipos de pet ou usuários.
package org.springframework.samples.petclinic.capabilities.owners;
