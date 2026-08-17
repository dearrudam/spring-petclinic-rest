/// # Especialidades
/// > Gerenciar o cadastro e o ciclo de vida das especialidades veterinárias.
///
/// ## Boundary
/// - `list-specialties` — listar as especialidades cadastradas
/// - `get-specialty` — consultar uma especialidade por seu identificador
/// - `add-specialty` — cadastrar uma especialidade
/// - `update-specialty` — alterar o nome de uma especialidade
/// - `delete-specialty` — excluir uma especialidade
///
/// ## Requirements
/// ### R1: Listar especialidades
/// - R1.1 — Quando a listagem for solicitada e houver especialidades cadastradas, a capacidade deverá fornecer todas as especialidades encontradas.
/// - R1.2 — Se a listagem for solicitada enquanto não houver especialidades cadastradas, então a capacidade deverá informar que nenhuma especialidade foi encontrada.
///
/// ### R2: Consultar especialidade
/// - R2.1 — Quando uma especialidade existente for solicitada por seu identificador, a capacidade deverá fornecer seus dados.
/// - R2.2 — Se não existir especialidade com o identificador solicitado, então a capacidade deverá informar que a especialidade não foi encontrada.
/// - R2.3 — Se um identificador negativo for informado para consulta, então a capacidade deverá rejeitar a solicitação.
///
/// ### R3: Cadastrar especialidade
/// - R3.1 — Quando um nome válido for informado, a capacidade deverá persistir a especialidade e atribuir um identificador.
/// - R3.2 — Se o nome estiver ausente, vazio ou exceder 80 caracteres, então a capacidade deverá rejeitar o cadastro.
///
/// ### R4: Alterar especialidade
/// - R4.1 — Quando um nome válido for informado para uma especialidade existente, a capacidade deverá atualizar o nome preservando sua identidade.
/// - R4.2 — Se não existir especialidade com o identificador informado, então a capacidade deverá informar que a especialidade não foi encontrada.
/// - R4.3 — Se o nome estiver ausente, vazio ou exceder 80 caracteres, então a capacidade deverá rejeitar a alteração.
/// - R4.4 — Se um identificador negativo for informado para alteração, então a capacidade deverá rejeitar a solicitação.
///
/// ### R5: Excluir especialidade
/// - R5.1 — Quando a exclusão de uma especialidade existente for solicitada, a capacidade deverá remover a especialidade.
/// - R5.2 — Se não existir especialidade com o identificador informado, então a capacidade deverá informar que a especialidade não foi encontrada.
/// - R5.3 — Se um identificador negativo for informado para exclusão, então a capacidade deverá rejeitar a solicitação.
///
/// ## Entities
/// - Specialty
///
/// ## Out of scope
/// - Associar especialidades a veterinários ou definir o resultado da exclusão de uma especialidade em uso.
/// - Gerenciar veterinários, pets, tipos de pet, proprietários, visitas ou usuários.
/// - Definir autenticação e autorização, que são preocupações transversais de segurança.
package org.springframework.samples.petclinic.capabilities.specialties;
