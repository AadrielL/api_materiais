package com.api.materiais.engine;

import com.api.materiais.dto.request.LevantamentoRequest;
import com.api.materiais.model.ItemMaterial;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class LevantamentoEngine {

    private final DimensionamentoFios dimensionamento = new DimensionamentoFios();
    private final CalculadoraCarga calculadoraCarga = new CalculadoraCarga();

    public List<ItemMaterial> calcularTudo(LevantamentoRequest request) {
        Map<String, Double> acumuladorMetros = new LinkedHashMap<>();
        List<ItemMaterial> outrosItens = new ArrayList<>();

        // Cálculo do percurso médio baseado na geometria da área
        double percursoMedio = Math.sqrt(request.areaTotalM2()) * 1.45;

        // 1. CHUVEIROS
        if (request.qtdChuveiros() > 0) {
            double corrente = dimensionamento.calcularCorrente(RegrasNBR5410.POTENCIA_CHUVEIRO, RegrasNBR5410.TENSÃO_PADRAO);
            String bitola = dimensionamento.selecionarCabo(corrente).replace(".", ",");
            double qtd = request.qtdChuveiros() * (percursoMedio + 32);
            registrarNoMapa(acumuladorMetros, bitola, qtd, true, false);
            outrosItens.add(new ItemMaterial("Disjuntor DIN p/ Chuveiro", (double) request.qtdChuveiros(), "UNID"));
        }

        // 2. AR-CONDICIONADO
        if (request.qtdQuartos() > 0) {
            double qtd = request.qtdQuartos() * (percursoMedio + 16);
            registrarNoMapa(acumuladorMetros, "4,0mm²", qtd, true, false);
            outrosItens.add(new ItemMaterial("Disjuntor DIN 20A p/ Ar-Condicionado", (double) request.qtdQuartos(), "UNID"));
        }

        // 3. ILUMINAÇÃO (Setorização por área)
        double metrosLuz = (request.areaTotalM2() * 1.2) + 40;
        registrarNoMapa(acumuladorMetros, "1,5mm²", metrosLuz, false, true);

        int qtdDisjLuz = (request.areaTotalM2() > 120) ? 3 : 2;
        outrosItens.add(new ItemMaterial("Disjuntor DIN 10A (Iluminação Setorizada)", (double) qtdDisjLuz, "UNID"));

        // 4. TOMADAS (Setorizadas: Cozinha, Sala e Área Íntima)
        // Cozinha e Área de Serviço
        double metrosCozinha = percursoMedio + 30;
        registrarNoMapa(acumuladorMetros, "2,5mm²", metrosCozinha, true, false);
        outrosItens.add(new ItemMaterial("Disjuntor DIN 20A (Tomadas Cozinha/Serviço)", 1.0, "UNID"));

        // Tomadas da Sala
        double metrosSala = percursoMedio + 20;
        registrarNoMapa(acumuladorMetros, "2,5mm²", metrosSala, true, false);
        outrosItens.add(new ItemMaterial("Disjuntor DIN 16A (Tomadas Sala/Social)", 1.0, "UNID"));

        // Tomadas Área Íntima (Cálculo dinâmico de circuitos)
        int qtdDisjuntoresIntimo = (int) Math.ceil((request.qtdPontosEletrica() - 5) / 8.0);
        if (qtdDisjuntoresIntimo < 1) qtdDisjuntoresIntimo = 1;

        double metrosIntimo = qtdDisjuntoresIntimo * (percursoMedio + 15);
        registrarNoMapa(acumuladorMetros, "2,5mm²", metrosIntimo, true, false);
        outrosItens.add(new ItemMaterial("Disjuntor DIN 16A (Tomadas Quartos/Banheiros)", (double) qtdDisjuntoresIntimo, "UNID"));

        // 5. ENTRADA E QUADRO (Cálculo de Demanda)
        double correnteGeral = dimensionamento.calcularCorrente(calculadoraCarga.calcularPotenciaEstimada(request), RegrasNBR5410.TENSÃO_PADRAO);
        String bitolaEntrada = dimensionamento.selecionarCabo(correnteGeral).replace(".", ",");
        double distPoste = request.distanciaQuadroPoste() > 0 ? request.distanciaQuadroPoste() : 15.0;
        registrarNoMapa(acumuladorMetros, bitolaEntrada + " (Entrada)", distPoste * 1.2, true, false);

        int disjGeral = (int) (Math.ceil(correnteGeral / 10.0) * 10.0 + 10);
        outrosItens.add(new ItemMaterial("Disjuntor Geral DIN " + Math.max(40, disjGeral) + "A", 1.0, "UNID"));
        outrosItens.add(new ItemMaterial("IDR Bipolar 40A", 1.0, "UNID"));
        outrosItens.add(new ItemMaterial("DPS 20kA", 3.0, "UNID"));
        outrosItens.add(new ItemMaterial("Barramento Pente Bifásico", 1.0, "UNID"));

        // Consolidação Final e Arredondamento Técnico
        List<ItemMaterial> itensFinais = new ArrayList<>();
        acumuladorMetros.forEach((desc, qtdTotal) -> {
            itensFinais.add(new ItemMaterial(desc, aplicarRegraArredondamento(qtdTotal), "METROS"));
        });
        itensFinais.addAll(outrosItens);

        return itensFinais;
    }

    private void registrarNoMapa(Map<String, Double> mapa, String bitola, double metros, boolean terra, boolean retorno) {
        String b = bitola.contains("mm²") ? bitola : bitola + "mm²";
        mapa.merge("Cabo " + b + " - FASE", metros, Double::sum);
        mapa.merge("Cabo " + b + " - NEUTRO", metros, Double::sum);
        if (terra) mapa.merge("Cabo " + b + " - TERRA", metros, Double::sum);
        if (retorno) mapa.merge("Cabo " + b + " - RETORNO", metros, Double::sum);
    }

    private double aplicarRegraArredondamento(double valor) {
        if (valor <= 0) return 0;
        return (valor <= 200) ? Math.ceil(valor / 10.0) * 10.0 : Math.ceil(valor / 50.0) * 50.0;
    }
}