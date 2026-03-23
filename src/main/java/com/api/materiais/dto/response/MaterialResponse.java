package com.api.materiais.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MaterialResponse {
    private String clienteNome;
    private List<ItemMaterialDTO> materiais;
    private Double valorTotalEstimado;
    private String avisoTecnico;
}