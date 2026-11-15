package com.api.materiais.dto.request;

public record CotacaoLogRequest(
        String profissionalId,
        String nomeProfissional,
        String tenantId,
        String cidade,
        String orcamentoId,
        String clienteNome,
        String distribuidoraDestino,
        String itensJson
) {}
