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
    private String tenantId;
    private String clienteNome;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "levantamento_id") // ADICIONE ISSO: evita criação de tabela extra
    private List<ItemMaterial> itens;
}