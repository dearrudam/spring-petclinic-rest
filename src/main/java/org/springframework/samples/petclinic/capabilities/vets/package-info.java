/// # Veterinarios
/// > Gerenciar o cadastro e as especialidades dos veterinarios da clinica.
///
/// ## Boundary
/// - `list-vets` — listar os veterinarios cadastrados
/// - `get-vet` — consultar um veterinario por seu identificador
/// - `add-vet` — cadastrar um veterinario
/// - `update-vet` — alterar os dados e as especialidades de um veterinario
/// - `delete-vet` — excluir um veterinario
///
/// ## Requirements
/// ### R1: Listar veterinarios
/// - R1.1 — Quando a listagem for solicitada e houver veterinarios cadastrados, a capacidade devera fornecer todos os veterinarios encontrados.
/// - R1.2 — Se a listagem for solicitada enquanto nao houver veterinarios cadastrados, entao a capacidade devera informar que nenhum veterinario foi encontrado.
///
/// ### R2: Consultar veterinario
/// - R2.1 — Quando um veterinario existente for solicitado por seu identificador, a capacidade devera fornecer seus dados e especialidades.
/// - R2.2 — Se nao existir veterinario com o identificador solicitado, entao a capacidade devera informar que o veterinario nao foi encontrado.
///
/// ### R3: Cadastrar veterinario
/// - R3.1 — Quando um veterinario com nome, sobrenome e especialidades validos for cadastrado, a capacidade devera persistir o veterinario, atribuir-lhe um identificador e informar sua localizacao.
/// - R3.2 — Se o nome ou o sobrenome estiver ausente, vazio, fora do formato ou fora do limite permitido, ou se a colecao de especialidades estiver ausente, entao a capacidade devera rejeitar o cadastro.
/// - R3.3 — Quando especialidades forem informadas no cadastro, a capacidade devera associar ao veterinario as especialidades cadastradas com os nomes informados.
///
/// ### R4: Alterar veterinario
/// - R4.1 — Quando novos nome, sobrenome e especialidades validos forem informados para um veterinario existente, a capacidade devera atualizar esses dados preservando sua identidade.
/// - R4.2 — Se nao existir veterinario com o identificador informado para alteracao, entao a capacidade devera informar que o veterinario nao foi encontrado.
/// - R4.3 — Se o nome ou o sobrenome da alteracao estiver ausente, vazio, fora do formato ou fora do limite permitido, ou se a colecao de especialidades estiver ausente, entao a capacidade devera rejeitar a alteracao.
///
/// ### R5: Excluir veterinario
/// - R5.1 — Quando a exclusao de um veterinario existente for solicitada, a capacidade devera remover o veterinario.
/// - R5.2 — Se nao existir veterinario com o identificador informado para exclusao, entao a capacidade devera informar que o veterinario nao foi encontrado.
///
/// ## Entities
/// - Vet
/// - Specialty
///
/// ## Out of scope
/// - Cadastrar, alterar ou excluir especialidades.
/// - Gerenciar proprietarios, pets, visitas, tipos de pet ou usuarios.
/// - Definir autenticacao e autorizacao, que sao preocupacoes transversais de seguranca.
package org.springframework.samples.petclinic.capabilities.vets;
