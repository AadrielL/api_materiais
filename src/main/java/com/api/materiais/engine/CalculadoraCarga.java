package com.api.materiais.engine;

import com.api.materiais.dto.request.LevantamentoRequest;

public class CalculadoraCarga {
    public double calcularPotenciaEstimada(LevantamentoRequest request) {
        // Cálculo simplificado de carga para definir circuitos
        double cargaTUGs = (request.getQtdQuartos() * 4 * RegrasNBR5410.VA_TOMADA_GERAL) +
                (request.getQtdCozinhas() * 3 * RegrasNBR5410.VA_TOMADA_COZINHA);
        double cargaTUEs = request.getQtdChuveiros() * 5500.0;
        return cargaTUGs + cargaTUEs;
    }
}