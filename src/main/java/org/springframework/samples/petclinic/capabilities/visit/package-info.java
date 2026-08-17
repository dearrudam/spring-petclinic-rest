/// # Visitas
/// > Gerenciar o registro e o ciclo de vida das visitas realizadas pelos pets.
///
/// ## Boundary
/// - `list-visits` — listar todas as visitas registradas
/// - `get-visit` — consultar uma visita por seu identificador
/// - `register-visit` — registrar uma visita associada a um pet
/// - `update-visit` — alterar a data e a descrição de uma visita
/// - `delete-visit` — excluir uma visita registrada
/// - `list-pet-visits` — listar as visitas associadas a um pet
///
/// ## Requirements
/// ### R1: Listar visitas
/// - R1.1 — Quando a listagem for solicitada e houver visitas registradas, a capacidade deverá fornecer todas as visitas encontradas.
/// - R1.2 — Se a listagem for solicitada enquanto não houver visitas registradas, então a capacidade deverá informar que nenhuma visita foi encontrada.
///
/// ### R2: Consultar visita
/// - R2.1 — Quando uma visita existente for solicitada por seu identificador, a capacidade deverá fornecer os dados da visita e o pet associado.
/// - R2.2 — Se não existir visita com o identificador solicitado, então a capacidade deverá informar que a visita não foi encontrada.
/// - R2.3 — Se um identificador negativo for informado para consulta, então a capacidade deverá rejeitar a solicitação.
///
/// ### R3: Registrar visita
/// - R3.1 — Quando uma visita com pet identificado e descrição válida for registrada, a capacidade deverá persistir a visita associada ao pet e atribuir um identificador à visita.
/// - R3.2 — Se a descrição estiver ausente, vazia ou exceder 255 caracteres, então a capacidade deverá rejeitar o registro da visita.
/// - R3.3 — Quando uma visita for registrada sem data informada, a capacidade deverá atribuir a data atual à visita.
/// - R3.4 — Quando uma visita com data atual ou futura e demais dados válidos for registrada, a capacidade deverá aceitar o registro.
/// - R3.5 — Se uma visita for registrada com data anterior à data atual, então a capacidade deverá rejeitar o registro.
/// - R3.6 — Se um identificador negativo de pet for informado, então a capacidade deverá rejeitar o registro.
///
/// ### R4: Alterar visita
/// - R4.1 — Quando novos valores válidos de data e descrição forem informados para uma visita existente, a capacidade deverá atualizar esses dados preservando a identidade e o pet associado.
/// - R4.2 — Se não existir visita com o identificador informado para alteração, então a capacidade deverá informar que a visita não foi encontrada.
/// - R4.3 — Se a nova descrição estiver ausente, vazia ou exceder 255 caracteres, então a capacidade deverá rejeitar a alteração.
/// - R4.4 — Se um identificador negativo for informado para alteração, então a capacidade deverá rejeitar a solicitação.
///
/// ### R5: Excluir visita
/// - R5.1 — Quando a exclusão de uma visita existente for solicitada, a capacidade deverá remover a visita.
/// - R5.2 — Se não existir visita com o identificador informado para exclusão, então a capacidade deverá informar que a visita não foi encontrada.
/// - R5.3 — Se um identificador negativo for informado para exclusão, então a capacidade deverá rejeitar a solicitação.
///
/// ### R6: Listar visitas de um pet
/// - R6.1 — Quando as visitas de um pet existente forem solicitadas, a capacidade deverá fornecer todas as visitas associadas ao pet, inclusive uma coleção vazia quando ele não possuir visitas.
///
/// ## Entities
/// - Visit
///
/// ## Out of scope
/// - Validar o vínculo entre o proprietário informado e o pet ao registrar uma visita pelo contexto do proprietário.
/// - Definir o resultado da listagem de visitas para um pet inexistente enquanto os perfis de persistência apresentarem comportamentos distintos.
/// - Gerenciar proprietários, pets, veterinários, especialidades ou tipos de pet.
package org.springframework.samples.petclinic.capabilities.visit;
