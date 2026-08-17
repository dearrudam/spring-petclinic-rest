/// # Erro de exemplo
/// > Produzir uma falha controlada para demonstrar a resposta de erro da API.
///
/// ## Boundary
/// - `produce-sample-error` — solicitar uma resposta de erro de exemplo
///
/// ## Requirements
/// ### R1: Produzir erro de exemplo
/// - R1.1 — Quando um erro de exemplo for solicitado, a capacidade deverá fornecer uma resposta de problema e não deverá fornecer uma resposta de sucesso.
///
/// ## Out of scope
/// - Representar erros produzidos pelas demais capacidades.
/// - Expor detalhes técnicos internos na resposta de problema.
package org.springframework.samples.petclinic.capabilities.oops;
