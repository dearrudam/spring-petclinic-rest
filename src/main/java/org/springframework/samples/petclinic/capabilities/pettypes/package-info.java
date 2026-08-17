/// # Tipos de pet
/// > Gerenciar o cadastro e o ciclo de vida dos tipos usados para classificar os pets.
///
/// ## Boundary
/// - `list-pet-types` — listar os tipos de pet cadastrados
/// - `get-pet-type` — consultar um tipo de pet por seu identificador
/// - `add-pet-type` — cadastrar um tipo de pet
/// - `update-pet-type` — alterar o nome de um tipo de pet
/// - `delete-pet-type` — excluir um tipo de pet
///
/// ## Requirements
/// ### R1: Listar tipos de pet
/// - R1.1 — Quando a listagem for solicitada e houver tipos de pet cadastrados, a capacidade deverá fornecer todos os tipos encontrados.
/// - R1.2 — Se a listagem for solicitada enquanto não houver tipos de pet cadastrados, então a capacidade deverá informar que nenhum tipo de pet foi encontrado.
///
/// ### R2: Consultar tipo de pet
/// - R2.1 — Quando um tipo de pet existente for solicitado por seu identificador, a capacidade deverá fornecer seus dados.
/// - R2.2 — Se não existir tipo de pet com o identificador solicitado, então a capacidade deverá informar que o tipo de pet não foi encontrado.
/// - R2.3 — Se um identificador negativo for informado para consulta, então a capacidade deverá rejeitar a solicitação.
///
/// ### R3: Cadastrar tipo de pet
/// - R3.1 — Quando um nome válido for informado, a capacidade deverá persistir o tipo de pet, atribuir um identificador e informar sua localização.
/// - R3.2 — Se o nome estiver ausente, vazio ou exceder 80 caracteres, então a capacidade deverá rejeitar o cadastro.
///
/// ### R4: Alterar tipo de pet
/// - R4.1 — Quando um nome válido for informado para um tipo de pet existente, a capacidade deverá atualizar o nome preservando sua identidade.
/// - R4.2 — Se não existir tipo de pet com o identificador informado, então a capacidade deverá informar que o tipo de pet não foi encontrado.
/// - R4.3 — Se o nome estiver ausente, vazio ou exceder 80 caracteres, então a capacidade deverá rejeitar a alteração.
/// - R4.4 — Se um identificador negativo for informado para alteração, então a capacidade deverá rejeitar a solicitação.
///
/// ### R5: Excluir tipo de pet
/// - R5.1 — Quando a exclusão de um tipo de pet existente for solicitada, a capacidade deverá remover o tipo de pet.
/// - R5.2 — Se não existir tipo de pet com o identificador informado, então a capacidade deverá informar que o tipo de pet não foi encontrado.
/// - R5.3 — Se um identificador negativo for informado para exclusão, então a capacidade deverá rejeitar a solicitação.
///
/// ## Entities
/// - PetType
///
/// ## Out of scope
/// - Definir o resultado da exclusão de um tipo que esteja associado a pets.
/// - Gerenciar pets, proprietários, visitas, veterinários, especialidades ou usuários.
/// - Definir autenticação e autorização, que são preocupações transversais de segurança.
package org.springframework.samples.petclinic.capabilities.pettypes;
