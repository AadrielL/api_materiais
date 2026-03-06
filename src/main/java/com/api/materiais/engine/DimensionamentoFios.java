package com.api.materiais.engine;

public class DimensionamentoFios {
    public String sugerirBitola(double corrente) {
        if (corrente <= 15) return "1,5mm²";
        if (corrente <= 21) return "2,5mm²";
        if (corrente <= 28) return "4,0mm²";
        return "6,0mm²";
    }
}