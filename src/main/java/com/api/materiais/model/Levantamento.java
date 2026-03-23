package com.api.materiais.model;

import jakarta.persistence.*;
import lombok.Data;
import java.util.List;

@Entity
@Data
public class Levantamento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ADICIONE ESTA LINHA: É ela que conecta o levantamento ao orçamento do Front-end
    private String orcamentoId;

    private String tenantId;
    private String clienteNome;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "levantamento_id")
    private List<ItemMaterial> itens;
}