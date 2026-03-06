package com.api.materiais.engine;

public class DimensionamentoFios {

    // Baseado na capacidade de condução de corrente (tabela 36 da NBR 5410)
    public double calcularCorrente(double potencia, double tensao) {
        return potencia / tensao;
    }

    public String selecionarCabo(double correnteAmper) {
        if (correnteAmper <= 15.5) return "1.5mm²";
        if (correnteAmper <= 21.0) return "2.5mm²";
        if (correnteAmper <= 28.0) return "4.0mm²";
        if (correnteAmper <= 36.0) return "6.0mm²";
        return "10.0mm² (Verificar demanda)";
    }
}