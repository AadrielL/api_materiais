package com.api.materiais.dto.request;
import java.util.UUID;

public record LevantamentoRequest(
        UUID orcamentoId,
        String clienteNome,
        String telefone,
        int qtdQuartos,
        int qtdSalas,
        int qtdCozinhas,
        int qtdBanheiros,
        int qtdChuveiros,
        int qtdPontosEletrica,
        int areaTotalM2,
        double distanciaQuadroPoste,
        boolean modoEconomico, // <--- Define se haverá jump de terra
        String observacoestécnicas
) {}