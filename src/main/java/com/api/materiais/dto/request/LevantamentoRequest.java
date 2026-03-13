package com.api.materiais.dto.request;

public record LevantamentoRequest(
        String clienteNome,
        String telefone,
        int qtdQuartos,
        int qtdSalas,
        int qtdCozinhas,
        int qtdBanheiros,
        int qtdChuveiros, // Necessário para a lógica de bitola de cabo
        int areaTotalM2,
        String observacoestécnicas
) {}