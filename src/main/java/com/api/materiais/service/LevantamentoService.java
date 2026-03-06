package com.api.materiais.service;

import com.api.materiais.dto.request.LevantamentoRequest;
import com.api.materiais.model.Levantamento;
import com.api.materiais.model.ItemMaterial;
import com.api.materiais.repository.LevantamentoRepository;
import com.api.materiais.engine.DimensionamentoFios;
import com.api.materiais.engine.RegrasNBR5410;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

@Service
public class LevantamentoService {

    private final LevantamentoRepository repository;
    private final DimensionamentoFios dimensionamento = new DimensionamentoFios();

    public LevantamentoService(LevantamentoRepository repository) {
        this.repository = repository;
    }

    public Levantamento gerarLevantamento(LevantamentoRequest request, String tenantId) {
        Levantamento levantamento = new Levantamento();
        levantamento.setTenantId(tenantId);
        levantamento.setClienteNome(request.getClienteNome());

        List<ItemMaterial> itens = new ArrayList<>();

        // Exemplo de lógica para Chuveiros (TUE)
        if (request.getQtdChuveiros() > 0) {
            ItemMaterial chuveiroCabo = new ItemMaterial();
            // 5500W / 220V = ~25A -> Cabo 4mm ou 6mm
            String bitola = dimensionamento.selecionarCabo(25.0);
            chuveiroCabo.setDescricao("Cabo Flexível " + bitola + " para Chuveiros");
            chuveiroCabo.setQuantidade(request.getQtdChuveiros() * 20.0); // 20m por chuveiro
            itens.add(chuveiroCabo);
        }

        // Pontos de Tomada e Iluminação
        int totalPontos = (request.getQtdQuartos() * 4) + (request.getQtdCozinhas() * 5);
        ItemMaterial eletroduto = new ItemMaterial();
        eletroduto.setDescricao("Eletroduto Corrugado 3/4 (Reforçado)");
        eletroduto.setQuantidade(Math.ceil((totalPontos * RegrasNBR5410.METROS_ELETRODUTO_POR_PONTO) / 50));
        itens.add(eletroduto);

        levantamento.setItens(itens);
        return repository.save(levantamento);
    }
}