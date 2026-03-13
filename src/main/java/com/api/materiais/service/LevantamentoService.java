package com.api.materiais.service;

import com.api.materiais.dto.request.LevantamentoRequest;
import com.api.materiais.model.Levantamento;
import com.api.materiais.model.ItemMaterial;
import com.api.materiais.repository.LevantamentoRepository;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

@Service
public class LevantamentoService {

    private final LevantamentoRepository repository;

    public LevantamentoService(LevantamentoRepository repository) {
        this.repository = repository;
    }

    public Levantamento gerarLevantamento(LevantamentoRequest request, String tenantId) {
        Levantamento levantamento = new Levantamento();
        levantamento.setTenantId(tenantId);
        levantamento.setClienteNome(request.clienteNome());

        List<ItemMaterial> itens = new ArrayList<>();

        // LÓGICA DE ESTIMATIVA PROFISSIONAL
        int pontos = request.qtdPontosEletrica() > 0 ? request.qtdPontosEletrica() : (int)(request.areaTotalM2() / 4);

        // 1. Mangueiras (Eletroduto 3/4) - Estimativa: 3.5m por ponto
        double metrosMangueira = pontos * 3.5;
        itens.add(new ItemMaterial("Eletroduto Corrugado 3/4 (Amarelo)", Math.ceil(metrosMangueira / 50.0), "ROLO 50M"));

        // 2. Fiação Geral (2,5mm) - Estimativa: (Metragem * 1.5) + (Pontos * 6m)
        double metrosFio25 = (request.areaTotalM2() * 1.5) + (pontos * 6);
        itens.add(new ItemMaterial("Cabo Flexível 2,5mm² (Cores)", Math.ceil(metrosFio25 / 100.0), "ROLO 100M"));

        // 3. Fiação Iluminação (1,5mm)
        double metrosFio15 = (request.areaTotalM2() * 1.1);
        itens.add(new ItemMaterial("Cabo Flexível 1,5mm² (Cores)", Math.ceil(metrosFio15 / 100.0), "ROLO 100M"));

        // 4. Circuitos Pesados (Chuveiro)
        if (request.qtdChuveiros() > 0) {
            itens.add(new ItemMaterial("Cabo Flexível 6,0mm² (Preto/Azul)", (double)(request.qtdChuveiros() * 40), "METROS"));
        }

        levantamento.setItens(itens);
        return repository.save(levantamento);
    }
}