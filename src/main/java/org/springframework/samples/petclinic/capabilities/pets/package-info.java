/// # Pets
/// > Gerenciar a consulta, a listagem, a alteração e a exclusão dos pets da clínica.
///
/// ## Boundary
/// - `list-pets` — listar os pets cadastrados, inclusive de forma paginada
/// - `get-pet` — consultar um pet por seu identificador
/// - `update-pet` — alterar os dados de um pet
/// - `delete-pet` — excluir um pet
///
/// ## Requirements
/// ### R1: Listar pets
/// - R1.1 — Quando a listagem for solicitada e houver pets cadastrados, a capacidade deverá fornecer todos os pets encontrados.
/// - R1.2 — Se a listagem for solicitada enquanto não houver pets cadastrados, então a capacidade deverá informar que nenhum pet foi encontrado.
/// - R1.3 — Quando a listagem paginada for solicitada, a capacidade deverá fornecer uma página de pets ordenados por identificador.
///
/// ### R2: Consultar pet
/// - R2.1 — Quando um pet existente for solicitado por seu identificador, a capacidade deverá fornecer os dados do pet, inclusive as visitas associadas.
/// - R2.2 — Se não existir pet com o identificador solicitado, então a capacidade deverá informar que o pet não foi encontrado.
///
/// ### R3: Alterar pet
/// - R3.1 — Quando novos nome, data de nascimento e tipo válidos forem informados para um pet existente, a capacidade deverá atualizar esses dados preservando a identidade do pet.
/// - R3.2 — Se não existir pet com o identificador informado para alteração, então a capacidade deverá informar que o pet não foi encontrado.
/// - R3.3 — Se o nome estiver ausente ou exceder 30 caracteres, se a data de nascimento estiver ausente, no futuro ou anterior a mais de 50 anos, ou se o tipo estiver ausente, então a capacidade deverá rejeitar a alteração.
///
/// ### R4: Excluir pet
/// - R4.1 — Quando a exclusão de um pet existente for solicitada, a capacidade deverá remover o pet.
/// - R4.2 — Se não existir pet com o identificador informado para exclusão, então a capacidade deverá informar que o pet não foi encontrado.
///
/// ## Entities
/// - Pet
///
/// ## Out of scope
/// - Cadastrar um pet, pois a criação de pet ocorre apenas pelo contexto do proprietário.
/// - Gerenciar tipos de pet, proprietários, visitas, veterinários, especialidades ou usuários.
/// - Definir autenticação e autorização, que são preocupações transversais de segurança.
package org.springframework.samples.petclinic.capabilities.pets;
