package com.api.materiais.controller;

import com.api.materiais.dto.request.LevantamentoRequest;
import com.api.materiais.model.Levantamento;
import com.api.materiais.service.LevantamentoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/materiais")
public class LevantamentoMaterialController {
    private final LevantamentoService service;

    public LevantamentoMaterialController(LevantamentoService service) {
        this.service = service;
    }

    @PostMapping("/gerar")
    public ResponseEntity<Levantamento> gerar(@RequestBody LevantamentoRequest req,
                                              @RequestHeader("X-Tenant-ID") String tenantId) {
        return ResponseEntity.ok(service.gerarParaCliente(req, tenantId));
    }
}