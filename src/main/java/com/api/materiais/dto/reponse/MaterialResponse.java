package com.api.materiais.dto.reponse;

import lombok.Data;
import java.util.List;

@Data
public class MaterialResponse {
    private String clienteNome;
    private List<ItemMaterialDTO> materiais;
    private Double valorTotalEstimado;
    private String avisoTecnico; // Ex: "Cálculo baseado em estimativa NBR 5410"
}