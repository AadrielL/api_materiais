package com.api.materiais.dto.request;

public record LevantamentoRequest(
        String clienteNome,
        String telefone,
        int qtdQuartos,
        int qtdSalas,
        int qtdCozinhas,
        int qtdBanheiros,
        int qtdChuveiros,
        int qtdPontosEletrica, // <--- Adicionado para a lógica de mangueiras/fios
        int areaTotalM2,
        String observacoestécnicas
) {}