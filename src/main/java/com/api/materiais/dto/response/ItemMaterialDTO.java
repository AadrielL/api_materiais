package com.api.materiais.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ItemMaterialDTO {
    private String descricao;
    private double quantidade;
    private String unidade;
}