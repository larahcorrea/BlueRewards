package br.com.fiap.bluerewards.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class CupomDto {

    private String descricao;
    private LocalDateTime validade;
    private int pontuacao;
    private EmpresaDto empresa;
    private boolean disponivel;
    
}
