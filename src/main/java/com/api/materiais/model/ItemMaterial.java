package com.api.materiais.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemMaterial {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String descricao;
    private Double quantidade;
    private String unidade;

    // ADICIONE ESTAS LINHAS AQUI:
    @ManyToOne
    @JoinColumn(name = "levantamento_id")
    private Levantamento levantamento;

    // Construtor auxiliar usado no Service
    public ItemMaterial(String descricao, Double quantidade, String unidade) {
        this.descricao = descricao;
        this.quantidade = quantidade;
        this.unidade = unidade;
    }
}