package com.api.materiais.service;

import com.api.materiais.dto.request.LevantamentoRequest;
import com.api.materiais.dto.reponse.ItemMaterialDTO;
import com.api.materiais.model.Levantamento;
import com.api.materiais.model.ItemMaterial;
import com.api.materiais.repository.LevantamentoRepository;
import com.api.materiais.engine.RegrasNBR5410;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

@Service
public class LevantamentoService {
    private final LevantamentoRepository repository;

    public LevantamentoService(LevantamentoRepository repository) {
        this.repository = repository;
    }

    public Levantamento gerarParaCliente(LevantamentoRequest request, String tenantId) {
        int pontos = (request.getQtdQuartos() * 5) + (request.getQtdCozinhas() * 6);

        Levantamento lev = new Levantamento();
        lev.setTenantId(tenantId);
        lev.setClienteNome(request.getClienteNome());

        List<ItemMaterial> itens = new ArrayList<>();
        // Exemplo: Cálculo de Cabos
        ItemMaterial cabo = new ItemMaterial();
        cabo.setDescricao("Cabo Flexível 2,5mm²");
        cabo.setQuantidade(Math.ceil((pontos * RegrasNBR5410.METROS_FIO_POR_PONTO) / 100));
        itens.add(cabo);

        lev.setItens(itens);
        return repository.save(lev);
    }
}