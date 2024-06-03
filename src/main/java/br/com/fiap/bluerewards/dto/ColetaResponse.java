package br.com.fiap.bluerewards.dto;

import br.com.fiap.bluerewards.model.Coleta;
import br.com.fiap.bluerewards.model.PontoColeta;

public record ColetaResponse(Long id, double peso, PontoColeta pontoColeta, UsuarioResponse usuarioResponse ) {
    
    public static ColetaResponse fromColeta(Coleta coleta){
        return new ColetaResponse(coleta.getId(), coleta.getPeso(), coleta.getPontoColeta(), UsuarioResponse.fromUsuario(coleta.getUsuario()));
    }
}
