package com.api.materiais.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class TabelaReferencia {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nomeMaterial;
    private Double precoBase;
    private String unidade; // ex: Metro, Rolo, Unidade
    private String tenantId; // Cada eletricista pode ter seus preços
}