/// # Usuários
/// > Cadastrar usuários e preparar seus papéis de acesso para persistência.
///
/// ## Boundary
/// - `add-user` — cadastrar um usuário com seus papéis de acesso
///
/// ## Requirements
/// ### R1: Cadastrar usuário
/// - R1.1 — Quando um usuário com nome e ao menos um papel de acesso válidos for cadastrado, a capacidade deverá persistir o usuário e fornecer seus dados.
/// - R1.2 — Se o nome do usuário estiver ausente, vazio ou exceder 80 caracteres, se a senha informada estiver vazia ou exceder 80 caracteres, ou se o nome de um papel estiver vazio ou exceder 80 caracteres, então a capacidade deverá rejeitar o cadastro.
/// - R1.3 — Se nenhum papel de acesso for informado, então a capacidade deverá rejeitar o cadastro.
/// - R1.4 — Quando um papel sem o prefixo `ROLE_` for informado, a capacidade deverá adicionar o prefixo antes de persistir o usuário.
///
/// ## Entities
/// - User
/// - Role
///
/// ## Out of scope
/// - Consultar, alterar ou excluir usuários.
/// - Autenticar usuários ou autorizar operações das demais capacidades.
/// - Gerenciar proprietários, pets, visitas, veterinários, especialidades ou tipos de pet.
package org.springframework.samples.petclinic.capabilities.users;
