package com.api.materiais.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/materiais/eletrotecnica")
public class FerramentasEletrotecnicaController {

    private static final double RHO_COBRE = 0.0172; // Ohm.mm2/m (Cobre a 20°C/70°C prático)
    private static final double[] SECOES_COMERCIAIS = {1.5, 2.5, 4.0, 6.0, 10.0, 16.0, 25.0, 35.0, 50.0, 70.0};
    // Capacidade de condução Iz (Método B1 NBR 5410 - PVC 70°C, 2 condutores carregados)
    private static final double[] AMPACIDADE_B1_2C = {17.5, 24.0, 32.0, 41.0, 57.0, 76.0, 101.0, 125.0, 151.0, 192.0};
    // Capacidade de condução Iz (Método B1 NBR 5410 - PVC 70°C, 3 condutores carregados)
    private static final double[] AMPACIDADE_B1_3C = {15.5, 21.0, 28.0, 36.0, 50.0, 68.0, 89.0, 110.0, 134.0, 171.0};
    private static final int[] DISJUNTORES_DIN = {10, 16, 20, 25, 32, 40, 50, 63, 80, 100, 125, 160, 200};

    /**
     * 1. Calculadora de Queda de Tensão, Seção de Condutor e Disjuntor (NBR 5410)
     */
    @PostMapping("/queda-tensao")
    public ResponseEntity<Map<String, Object>> calcularQuedaTensao(@RequestBody Map<String, Object> body) {
        double potenciaWatts = toDouble(body.get("potenciaWatts"), 2200);
        double tensaoVolts = toDouble(body.get("tensaoVolts"), 220);
        double distanciaMetros = toDouble(body.get("distanciaMetros"), 20);
        String sistema = String.valueOf(body.getOrDefault("sistema", "MONOFASICO")).toUpperCase();
        double fatorPotencia = toDouble(body.get("fatorPotencia"), 0.92);
        double limiteQuedaPct = toDouble(body.get("limiteQuedaPercentual"), 4.0);
        String finalidade = String.valueOf(body.getOrDefault("finalidade", "TOMADAS_FORCA")).toUpperCase();

        if (fatorPotencia <= 0 || fatorPotencia > 1) fatorPotencia = 0.92;
        if (tensaoVolts <= 0) tensaoVolts = 220;
        if (limiteQuedaPct <= 0) limiteQuedaPct = 4.0;

        boolean trifasico = "TRIFASICO".equals(sistema);
        double fatorCircuito = trifasico ? Math.sqrt(3.0) : 2.0;

        // Corrente de projeto Ib
        double correnteIb = trifasico
                ? potenciaWatts / (Math.sqrt(3.0) * tensaoVolts * fatorPotencia)
                : potenciaWatts / (tensaoVolts * fatorPotencia);

        // Seção mínima normativa (NBR 5410 Tab. 47: 1,5 mm² iluminação | 2,5 mm² força/tomadas)
        double secaoMinimaNorma = "ILUMINACAO".equals(finalidade) ? 1.5 : 2.5;

        // Seção mínima por queda de tensão: S = (fator * rho * L * Ib * cosPhi) / deltaV_max
        double deltaVMaxVolts = (limiteQuedaPct / 100.0) * tensaoVolts;
        double secaoCalculadaQueda = (fatorCircuito * RHO_COBRE * distanciaMetros * correnteIb * fatorPotencia) / deltaVMaxVolts;

        double[] tabelaIz = trifasico ? AMPACIDADE_B1_3C : AMPACIDADE_B1_2C;

        double secaoEscolhida = SECOES_COMERCIAIS[SECOES_COMERCIAIS.length - 1];
        double izEscolhido = tabelaIz[tabelaIz.length - 1];

        for (int i = 0; i < SECOES_COMERCIAIS.length; i++) {
            double s = SECOES_COMERCIAIS[i];
            double iz = tabelaIz[i];
            if (s >= secaoMinimaNorma && s >= secaoCalculadaQueda && iz >= correnteIb) {
                secaoEscolhida = s;
                izEscolhido = iz;
                break;
            }
        }

        // Disjuntor comercial In: Ib <= In <= Iz
        int disjuntorIn = DISJUNTORES_DIN[DISJUNTORES_DIN.length - 1];
        for (int d : DISJUNTORES_DIN) {
            if (d >= Math.ceil(correnteIb)) {
                disjuntorIn = d;
                break;
            }
        }

        // Se o disjuntor comercial ultrapassar Iz da bitola, sobe um degrau de cabo para garantir In <= Iz
        if (disjuntorIn > izEscolhido) {
            for (int i = 0; i < SECOES_COMERCIAIS.length; i++) {
                if (tabelaIz[i] >= disjuntorIn && SECOES_COMERCIAIS[i] >= secaoEscolhida) {
                    secaoEscolhida = SECOES_COMERCIAIS[i];
                    izEscolhido = tabelaIz[i];
                    break;
                }
            }
        }

        double quedaVoltsReal = (fatorCircuito * RHO_COBRE * distanciaMetros * correnteIb * fatorPotencia) / secaoEscolhida;
        double quedaPctReal = (quedaVoltsReal / tensaoVolts) * 100.0;

        String tipoDisjuntor = trifasico ? "Tripolar DIN " : ("BIFASICO".equals(sistema) ? "Bipolar DIN " : "Monopolar DIN ");

        Map<String, Object> resp = new LinkedHashMap<>();
        resp.put("correnteProjetoAmperes", round2(correnteIb));
        resp.put("secaoRecomendadaMm2", secaoEscolhida);
        resp.put("capacidadeConducaoCaboAmperes", izEscolhido);
        resp.put("disjuntorRecomendado", tipoDisjuntor + disjuntorIn + "A (Curva C)");
        resp.put("disjuntorAmperes", disjuntorIn);
        resp.put("quedaTensaoRealPercentual", round2(quedaPctReal));
        resp.put("quedaTensaoRealVolts", round2(quedaVoltsReal));
        resp.put("limiteNormativoPercentual", limiteQuedaPct);
        resp.put("conformeNbr5410", quedaPctReal <= limiteQuedaPct && correnteIb <= disjuntorIn && disjuntorIn <= izEscolhido);
        resp.put("notaTecnica", String.format(
                Locale.US,
                "Critério NBR 5410 (Ib=%.1fA <= In=%dA <= Iz=%.1fA) com queda de %.2f%% em %.0fm.",
                correnteIb, disjuntorIn, izEscolhido, quedaPctReal, distanciaMetros
        ));
        return ResponseEntity.ok(resp);
    }

    /**
     * 2. Dimensionamento de Taxa de Ocupação de Eletrodutos (NBR 5410 item 6.2.11.1.6)
     */
    @PostMapping("/eletroduto")
    public ResponseEntity<Map<String, Object>> dimensionarEletroduto(@RequestBody Map<String, Object> body) {
        double bitola1 = toDouble(body.get("bitolaPrincipalMm2"), 2.5);
        int qtd1 = (int) toDouble(body.get("quantidadePrincipal"), 3);
        double bitola2 = toDouble(body.get("bitolaSecundariaMm2"), 0);
        int qtd2 = (int) toDouble(body.get("quantidadeSecundaria"), 0);

        int totalCondutores = Math.max(0, qtd1) + Math.max(0, qtd2);
        if (totalCondutores == 0) totalCondutores = 3;

        double areaTotalCabosMm2 = (areaExternaCaboPvc750V(bitola1) * Math.max(0, qtd1))
                + (bitola2 > 0 ? areaExternaCaboPvc750V(bitola2) * Math.max(0, qtd2) : 0);

        // Limite NBR 5410: 53% (1 condutor), 31% (2 condutores), 40% (3 ou mais condutores)
        double taxaMaximaNorma = totalCondutores == 1 ? 0.53 : (totalCondutores == 2 ? 0.31 : 0.40);

        // Eletrodutos comerciais: nome, diametro interno util (mm) e area interna total (mm²)
        Object[][] eletrodutos = {
                {"20mm (1/2\")", 15.0, Math.PI * Math.pow(7.5, 2)},
                {"25mm (3/4\")", 19.4, Math.PI * Math.pow(9.7, 2)},
                {"32mm (1\")", 25.4, Math.PI * Math.pow(12.7, 2)},
                {"40mm (1.1/4\")", 32.8, Math.PI * Math.pow(16.4, 2)},
                {"50mm (1.1/2\")", 41.2, Math.PI * Math.pow(20.6, 2)},
                {"60mm (2\")", 51.4, Math.PI * Math.pow(25.7, 2)}
        };

        String eletrodutoRecomendado = "60mm (2\")";
        double areaInternaEscolhida = (double) eletrodutos[eletrodutos.length - 1][2];

        for (Object[] el : eletrodutos) {
            double areaInterna = (double) el[2];
            if (areaTotalCabosMm2 <= areaInterna * taxaMaximaNorma) {
                eletrodutoRecomendado = (String) el[0];
                areaInternaEscolhida = areaInterna;
                break;
            }
        }

        double ocupacaoRealPct = (areaTotalCabosMm2 / areaInternaEscolhida) * 100.0;

        Map<String, Object> resp = new LinkedHashMap<>();
        resp.put("eletrodutoRecomendado", eletrodutoRecomendado);
        resp.put("totalCondutores", totalCondutores);
        resp.put("areaOcupadaCabosMm2", round2(areaTotalCabosMm2));
        resp.put("areaInternaEletrodutoMm2", round2(areaInternaEscolhida));
        resp.put("taxaOcupacaoRealPercentual", round2(ocupacaoRealPct));
        resp.put("taxaMaximaNbr5410Percentual", round2(taxaMaximaNorma * 100.0));
        resp.put("conformeNbr5410", ocupacaoRealPct <= (taxaMaximaNorma * 100.0));
        return ResponseEntity.ok(resp);
    }

    /**
     * 3. Calculadora de Campo para Ar-Condicionado (BTU/h) e Motores Elétricos (CV)
     */
    @PostMapping("/motor-ar")
    public ResponseEntity<Map<String, Object>> calcularMotorOuAr(@RequestBody Map<String, Object> body) {
        String tipo = String.valueOf(body.getOrDefault("tipoEquipamento", "AR_CONDICIONADO")).toUpperCase();
        double valor = toDouble(body.get("valor"), 12000);
        double tensaoVolts = toDouble(body.get("tensaoVolts"), 220);
        double distanciaMetros = toDouble(body.get("distanciaMetros"), 15);

        double potenciaEletricaWatts;
        double fatorPotencia = 0.88;
        String descricaoEquipamento;

        if ("MOTOR_CV".equals(tipo)) {
            // 1 CV = 735.5 W, rendimento médio ~0.82
            potenciaEletricaWatts = (valor * 735.5) / 0.82;
            fatorPotencia = 0.82;
            descricaoEquipamento = valor + " CV (Motor Elétrico Indução)";
        } else {
            // Ar-Condicionado Split/Inverter: média prática de ~0.095 W elétricos por BTU/h
            potenciaEletricaWatts = valor * 0.095;
            fatorPotencia = 0.92;
            descricaoEquipamento = (int) valor + " BTU/h (Ar-Condicionado)";
        }

        Map<String, Object> reqQueda = new HashMap<>();
        reqQueda.put("potenciaWatts", potenciaEletricaWatts);
        reqQueda.put("tensaoVolts", tensaoVolts);
        reqQueda.put("distanciaMetros", distanciaMetros);
        reqQueda.put("sistema", tensaoVolts >= 380 ? "TRIFASICO" : "BIFASICO");
        reqQueda.put("fatorPotencia", fatorPotencia);
        reqQueda.put("limiteQuedaPercentual", 4.0);
        reqQueda.put("finalidade", "TOMADAS_FORCA");

        Map<String, Object> base = new LinkedHashMap<>(Objects.requireNonNull(calcularQuedaTensao(reqQueda).getBody()));
        base.put("equipamento", descricaoEquipamento);
        base.put("potenciaEletricaEstimadaWatts", round2(potenciaEletricaWatts));
        base.put("correntePartidaEstimadaAmperes", round2(toDouble(base.get("correnteProjetoAmperes"), 0) * ("MOTOR_CV".equals(tipo) ? 5.5 : 1.4)));
        return ResponseEntity.ok(base);
    }

    /**
     * 4. Dimensionamento Rápido de QDC, Padrão de Entrada, IDR (30mA) e DPS (NBR 5410)
     */
    @PostMapping("/qdc-padrao")
    public ResponseEntity<Map<String, Object>> dimensionarQdc(@RequestBody Map<String, Object> body) {
        double cargaKw = toDouble(body.get("cargaInstaladaKw"), 15.0);
        double tensaoFaseNeutro = toDouble(body.get("tensaoFaseNeutro"), 220.0);
        int qtdCircuitos = (int) toDouble(body.get("quantidadeCircuitosTerminais"), 8);

        // Fator de demanda médio residencial/comercial leve
        double fatorDemanda = cargaKw <= 10 ? 0.85 : (cargaKw <= 25 ? 0.72 : 0.62);
        double demandaKva = (cargaKw * fatorDemanda) / 0.92;

        String modalidadePadrao;
        double correnteGeral;
        int polos;

        if (cargaKw <= 12.0) {
            modalidadePadrao = "Monofásico (Fase + Neutro + Terra)";
            correnteGeral = (demandaKva * 1000.0) / tensaoFaseNeutro;
            polos = 1;
        } else if (cargaKw <= 25.0) {
            modalidadePadrao = "Bifásico (2 Fases + Neutro + Terra)";
            double tensaoLinha = tensaoFaseNeutro == 127.0 ? 220.0 : 380.0;
            correnteGeral = (demandaKva * 1000.0) / tensaoLinha;
            polos = 2;
        } else {
            modalidadePadrao = "Trifásico (3 Fases + Neutro + Terra)";
            double tensaoLinha = tensaoFaseNeutro == 127.0 ? 220.0 : 380.0;
            correnteGeral = (demandaKva * 1000.0) / (Math.sqrt(3.0) * tensaoLinha);
            polos = 3;
        }

        int disjuntorGeral = 40;
        for (int d : DISJUNTORES_DIN) {
            if (d >= Math.max(32, Math.ceil(correnteGeral))) {
                disjuntorGeral = d;
                break;
            }
        }

        int idrNominal = disjuntorGeral <= 40 ? 40 : (disjuntorGeral <= 63 ? 63 : 80);
        double caboEntradaMm2 = disjuntorGeral <= 40 ? 6.0 : (disjuntorGeral <= 50 ? 10.0 : (disjuntorGeral <= 63 ? 16.0 : 25.0));

        // NBR 5410 item 6.5.4.7: Reserva mínima no QDC (até 6 circuitos -> +2; 7 a 12 -> +3; 13 a 30 -> +4; >30 -> +15%)
        int reservaCircuitos = qtdCircuitos <= 6 ? 2 : (qtdCircuitos <= 12 ? 3 : (qtdCircuitos <= 30 ? 4 : (int) Math.ceil(qtdCircuitos * 0.15)));
        int modulosMinimosQdc = qtdCircuitos + reservaCircuitos + polos + 4 + (polos + 1);
        int tamanhoComercialQdc = modulosMinimosQdc <= 12 ? 12 : (modulosMinimosQdc <= 18 ? 18 : (modulosMinimosQdc <= 24 ? 24 : 36));

        Map<String, Object> resp = new LinkedHashMap<>();
        resp.put("modalidadeFornecimento", modalidadePadrao);
        resp.put("demandaCalculadaKva", round2(demandaKva));
        resp.put("correnteDemandaAmperes", round2(correnteGeral));
        resp.put("disjuntorGeral", disjuntorGeral + "A (" + (polos == 1 ? "Monopolar" : polos == 2 ? "Bipolar" : "Tripolar") + " DIN)");
        resp.put("idrRecomendado", "IDR " + (polos == 1 ? "Bipolar " : "Tetrapolar ") + idrNominal + "A / 30mA (Classe AC ou A)");
        resp.put("dpsRecomendado", (polos + 1) + "x DPS Classe II 275V 20kA/45kA (Fases + Neutro)");
        resp.put("caboEntradaAterramentoMm2", caboEntradaMm2 + " mm² (Cobre 750V)");
        resp.put("reservaObrigatoriaNbr5410", reservaCircuitos + " circuitos livres");
        resp.put("quadroDistribuicaoSugerido", "QDC Embutir/Sobrepor para " + tamanhoComercialQdc + " módulos DIN com barramento pente");
        return ResponseEntity.ok(resp);
    }

    private double areaExternaCaboPvc750V(double secaoNominalMm2) {
        // Diâmetro externo aproximado (cobre + isolação PVC 750V) em mm
        double diametroExternoMm;
        if (secaoNominalMm2 <= 1.5) diametroExternoMm = 3.0;
        else if (secaoNominalMm2 <= 2.5) diametroExternoMm = 3.6;
        else if (secaoNominalMm2 <= 4.0) diametroExternoMm = 4.2;
        else if (secaoNominalMm2 <= 6.0) diametroExternoMm = 4.9;
        else if (secaoNominalMm2 <= 10.0) diametroExternoMm = 6.2;
        else if (secaoNominalMm2 <= 16.0) diametroExternoMm = 7.4;
        else if (secaoNominalMm2 <= 25.0) diametroExternoMm = 9.2;
        else diametroExternoMm = 10.8;
        return Math.PI * Math.pow(diametroExternoMm / 2.0, 2);
    }

    private double toDouble(Object val, double padrao) {
        if (val == null) return padrao;
        try {
            return Double.parseDouble(String.valueOf(val).replace(",", "."));
        } catch (Exception e) {
            return padrao;
        }
    }

    private double round2(double val) {
        return Math.round(val * 100.0) / 100.0;
    }
}
