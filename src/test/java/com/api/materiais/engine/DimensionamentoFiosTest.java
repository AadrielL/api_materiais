package com.api.materiais.engine;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DimensionamentoFiosTest {

    private DimensionamentoFios dimensionamentoFios;

    @BeforeEach
    void setUp() {
        dimensionamentoFios = new DimensionamentoFios();
    }

    @Test
    @DisplayName("Deve calcular corrente elétrica corretamente baseado em potência e tensão")
    void deveCalcularCorrenteComSucesso() {
        double potencia = 2200.0; // 2200W
        double tensao = 220.0;    // 220V

        double corrente = dimensionamentoFios.calcularCorrente(potencia, tensao);

        assertEquals(10.0, corrente, 0.01);
    }

    @Test
    @DisplayName("Deve selecionar cabo 1.5mm² para correntes até 15.5A (NBR 5410)")
    void deveSelecionarCabo1_5mm() {
        String cabo = dimensionamentoFios.selecionarCabo(12.0);
        assertEquals("1.5mm²", cabo);
    }

    @Test
    @DisplayName("Deve selecionar cabo 2.5mm² para correntes até 21.0A (NBR 5410)")
    void deveSelecionarCabo2_5mm() {
        String cabo = dimensionamentoFios.selecionarCabo(20.0);
        assertEquals("2.5mm²", cabo);
    }

    @Test
    @DisplayName("Deve selecionar cabo 6.0mm² para correntes até 36.0A (ex: Chuveiro 220V 7500W)")
    void deveSelecionarCabo6_0mm() {
        String cabo = dimensionamentoFios.selecionarCabo(34.09);
        assertEquals("6.0mm²", cabo);
    }
}