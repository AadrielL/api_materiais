package com.api.materiais.controller;

import com.api.materiais.dto.request.CotacaoLogRequest;
import com.api.materiais.dto.request.LevantamentoRequest;
import com.api.materiais.dto.response.MaterialResponse;
import com.api.materiais.model.CotacaoLog;
import com.api.materiais.repository.CotacaoLogRepository;
import com.api.materiais.service.LevantamentoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/materiais")
@CrossOrigin(originPatterns = "*", allowCredentials = "true", allowedHeaders = "*")
public class LevantamentoMaterialController {

    private final LevantamentoService service;
    private final CotacaoLogRepository cotacaoLogRepository;

    public LevantamentoMaterialController(LevantamentoService service, CotacaoLogRepository cotacaoLogRepository) {
        this.service = service;
        this.cotacaoLogRepository = cotacaoLogRepository;
    }

    @PostMapping("/gerar")
    public ResponseEntity<MaterialResponse> gerar(
            @RequestBody LevantamentoRequest req,
            @RequestHeader("X-Tenant-ID") String tenantId) {

        return ResponseEntity.ok(service.gerarLevantamento(req, tenantId));
    }

    @GetMapping("/detalhes/{orcamentoId}")
    public ResponseEntity<MaterialResponse> getDetalhes(@PathVariable String orcamentoId) {
        return ResponseEntity.ok(service.buscarPorOrcamentoId(orcamentoId.trim()));
    }

    // M2-04: Registra evento de intenção de cotação no banco antes de redirecionar para o WhatsApp da distribuidora
    @PostMapping("/cotacao-log")
    public ResponseEntity<CotacaoLog> registrarLogCotacao(
            @RequestBody CotacaoLogRequest req,
            @RequestHeader(value = "X-Tenant-ID", defaultValue = "default") String headerTenantId) {

        CotacaoLog log = new CotacaoLog();
        log.setProfissionalId(req.profissionalId());
        log.setNomeProfissional(req.nomeProfissional());
        log.setTenantId(req.tenantId() != null && !req.tenantId().isBlank() ? req.tenantId() : headerTenantId);
        log.setCidade(req.cidade() != null && !req.cidade().isBlank() ? req.cidade() : "Palmas/TO");
        log.setOrcamentoId(req.orcamentoId());
        log.setClienteNome(req.clienteNome());
        log.setDistribuidoraDestino(
                req.distribuidoraDestino() != null && !req.distribuidoraDestino().isBlank()
                        ? req.distribuidoraDestino()
                        : "Capital Elétrica - Palmas/TO"
        );
        log.setItensJson(req.itensJson());
        log.setDataHoraCotacao(LocalDateTime.now());

        return ResponseEntity.ok(cotacaoLogRepository.save(log));
    }

    @GetMapping("/cotacao-log")
    public ResponseEntity<List<CotacaoLog>> listarLogsCotacao(
            @RequestHeader(value = "X-Tenant-ID", defaultValue = "default") String tenantId) {
        return ResponseEntity.ok(cotacaoLogRepository.findByTenantIdOrderByDataHoraCotacaoDesc(tenantId));
    }
}
