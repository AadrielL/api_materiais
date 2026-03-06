package com.api.materiais.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class ItemMaterial {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String descricao;
    private double quantidade;
}