package com.api.materiais.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "cotacao_logs")
@Data
public class CotacaoLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String profissionalId;
    private String nomeProfissional;
    private String tenantId;
    private String cidade;
    private String orcamentoId;
    private String clienteNome;
    private String distribuidoraDestino;

    @Column(columnDefinition = "TEXT")
    private String itensJson;

    private LocalDateTime dataHoraCotacao = LocalDateTime.now();
}
