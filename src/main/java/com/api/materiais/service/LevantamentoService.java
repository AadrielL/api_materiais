package com.api.materiais.service;

import com.api.materiais.dto.request.LevantamentoRequest;
import com.api.materiais.dto.response.ItemMaterialDTO;
import com.api.materiais.dto.response.MaterialResponse;
import com.api.materiais.engine.LevantamentoEngine;
import com.api.materiais.model.Levantamento;
import com.api.materiais.model.ItemMaterial;
import com.api.materiais.repository.LevantamentoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class LevantamentoService {

    private final LevantamentoRepository repository;
    private final LevantamentoEngine engine; // Injeção da Engine técnica

    // Uso de construtor resolve o aviso "Private field is never assigned"
    public LevantamentoService(LevantamentoRepository repository, LevantamentoEngine engine) {
        this.repository = repository;
        this.engine = engine;
    }

    @Transactional
    public MaterialResponse gerarLevantamento(LevantamentoRequest request, String tenantId) {
        // Converte o ID do snapshot para String para busca no banco
        String orcamentoId = request.orcamentoId() != null ? request.orcamentoId().toString() : null;

        // 1. Limpeza de histórico: Mantém apenas os 2 últimos registros por orçamento
        if (orcamentoId != null) {
            List<Levantamento> existentes = repository.findByOrcamentoIdOrderByIdAsc(orcamentoId);
            if (existentes.size() >= 2) {
                repository.delete(existentes.get(0));
            }
        }

        // 2. Inicializa a entidade principal
        Levantamento levantamento = new Levantamento();
        levantamento.setTenantId(tenantId);
        levantamento.setClienteNome(request.clienteNome());
        levantamento.setOrcamentoId(orcamentoId);

        // 3. Executa a lógica da NBR 5410 delegando para a Engine
        // Toda a sua lógica de Chuveiros, TUGs, TUEs e Disjuntores agora roda aqui
        List<ItemMaterial> itensCalculados = engine.calcularTudo(request);

        // 4. Vincula os itens ao levantamento (Relacionamento JPA)
        itensCalculados.forEach(item -> item.setLevantamento(levantamento));
        levantamento.setItens(itensCalculados);

        // 5. Persistência e Resposta
        Levantamento salvo = repository.save(levantamento);
        return converterParaResponse(salvo);
    }

    public MaterialResponse buscarPorOrcamentoId(String orcamentoId) {
        List<Levantamento> resultados = repository.findByOrcamentoIdOrderByIdAsc(orcamentoId);
        if (resultados.isEmpty()) {
            throw new RuntimeException("Nenhum levantamento técnico encontrado para o ID: " + orcamentoId);
        }
        // Retorna o cálculo mais recente (último da lista)
        return converterParaResponse(resultados.get(resultados.size() - 1));
    }

    private MaterialResponse converterParaResponse(Levantamento levantamento) {
        return MaterialResponse.builder()
                .clienteNome(levantamento.getClienteNome())
                .avisoTecnico("Levantamento gerado via Snapshot Técnico (Norma NBR 5410).")
                .materiais(levantamento.getItens().stream()
                        .map(i -> new ItemMaterialDTO(i.getDescricao(), i.getQuantidade(), i.getUnidade()))
                        .collect(Collectors.toList()))
                .build();
    }
}