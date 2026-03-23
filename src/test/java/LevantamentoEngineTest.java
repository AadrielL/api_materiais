package com.api.materiais.engine;

import com.api.materiais.dto.request.LevantamentoRequest;
import com.api.materiais.model.ItemMaterial;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class LevantamentoEngineTest {

    private LevantamentoEngine engine;

    @BeforeEach
    void setup() {
        this.engine = new LevantamentoEngine();
    }

    @Test
    @DisplayName("Deve calcular disjuntor e cabos corretamente para 1 chuveiro")
    void deveCalcularMateriaisParaChuveiro() {
        // Cenário: Casa de 100m², 1 chuveiro, 3 quartos, 20 pontos elétricos
        LevantamentoRequest request = new LevantamentoRequest(
                "João Silva", 100.0, 3, 1, 1, 15, UUID.randomUUID(), 20
        );

        List<ItemMaterial> resultado = engine.calcularTudo(request);

        // Validação 1: Deve conter o disjuntor de chuveiro
        boolean temDisjuntorChuveiro = resultado.stream()
                .anyMatch(item -> item.getDescricao().equals("Disjuntor DIN p/ Chuveiro"));

        // Validação 2: Deve conter o cabo de entrada
        boolean temCaboEntrada = resultado.stream()
                .anyMatch(item -> item.getDescricao().contains("(Entrada)"));

        assertTrue(temDisjuntorChuveiro, "Deveria ter gerado o disjuntor do chuveiro");
        assertTrue(temCaboEntrada, "Deveria ter calculado o cabo de entrada");
    }

    @Test
    @DisplayName("Deve aplicar regra de arredondamento para 50 metros acima de 200m")
    void deveArredondarParaCinquentaMetros() {
        // Simulando um valor que resultaria em 215 metros (deve arredondar para 250)
        // Usamos um método de reflexão ou apenas testamos o retorno da lista
        LevantamentoRequest request = new LevantamentoRequest(
                "Teste Arredondamento", 300.0, 5, 2, 2, 20, UUID.randomUUID(), 40
        );

        List<ItemMaterial> resultado = engine.calcularTudo(request);

        // Verifica se algum cabo tem quantidade múltipla de 50 (arredondamento para cima)
        for (ItemMaterial item : resultado) {
            if (item.getUnidade().equals("METROS") && item.getQuantidade() > 200) {
                assertEquals(0, item.getQuantidade() % 50,
                        "Quantidade acima de 200m deve ser múltipla de 50. Erro no item: " + item.getDescricao());
            }
        }
    }
}