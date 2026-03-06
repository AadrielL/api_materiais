package com.api.materiais.dto.request;

import lombok.Data;

@Data
public class LevantamentoRequest {
    private String clienteNome;
    private int qtdQuartos;
    private int qtdBanheiros;
    private int qtdCozinhas;
    private int qtdChuveiros;
    private boolean incluirArCondicionado;
}