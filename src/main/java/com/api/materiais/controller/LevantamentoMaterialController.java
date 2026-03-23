package com.api.materiais.controller;

import com.api.materiais.dto.request.LevantamentoRequest;
import com.api.materiais.dto.response.MaterialResponse;
import com.api.materiais.service.LevantamentoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/materiais")
@CrossOrigin(originPatterns = "*", allowCredentials = "true", allowedHeaders = "*")
public class LevantamentoMaterialController {

    private final LevantamentoService service;

    public LevantamentoMaterialController(LevantamentoService service) {
        this.service = service;
    }

    @PostMapping("/gerar")
    public ResponseEntity<MaterialResponse> gerar(
            @RequestBody LevantamentoRequest req,
            @RequestHeader("X-Tenant-ID") String tenantId) {

        // Chamada direta para o service que já trata a limpeza de histórico (máximo 2)
        return ResponseEntity.ok(service.gerarLevantamento(req, tenantId));
    }

    @GetMapping("/detalhes/{orcamentoId}")
    public ResponseEntity<MaterialResponse> getDetalhes(@PathVariable String orcamentoId) {
        /*
         * Removido o UUID.fromString aqui no Controller para evitar erros de
         * "Invalid UUID string" caso o ID venha com espaços ou formato diferente.
         * O Service agora recebe String e trata a busca.
         */
        return ResponseEntity.ok(service.buscarPorOrcamentoId(orcamentoId.trim()));
    }
}