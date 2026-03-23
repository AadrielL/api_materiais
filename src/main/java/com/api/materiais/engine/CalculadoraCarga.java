package com.api.materiais.engine;

import com.api.materiais.dto.request.LevantamentoRequest;

public class CalculadoraCarga {
    public double calcularPotenciaEstimada(LevantamentoRequest request) {
        // Carga de Tomadas Uso Geral
        double cargaTUGs = (request.qtdQuartos() * 3 * RegrasNBR5410.VA_TOMADA_GERAL) +
                (request.qtdSalas() * 3 * RegrasNBR5410.VA_TOMADA_GERAL);

        // Carga de Cozinhas (Regra 3x 600VA)
        double cargaCozinha = (request.qtdCozinhas() * 3 * RegrasNBR5410.VA_TOMADA_COZINHA);

        // TUEs - Chuveiros
        double cargaChuveiros = request.qtdChuveiros() * RegrasNBR5410.POTENCIA_CHUVEIRO;

        // TUEs - Ar Condicionado (Estimativa: 1 por quarto)
        double cargaAr = request.qtdQuartos() * RegrasNBR5410.POTENCIA_AR_CONDICIONADO;

        return cargaTUGs + cargaCozinha + cargaChuveiros + cargaAr;
    }
}