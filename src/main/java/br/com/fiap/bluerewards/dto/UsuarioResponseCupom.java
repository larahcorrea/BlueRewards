package br.com.fiap.bluerewards.dto;

import java.util.Set;

import br.com.fiap.bluerewards.model.Cupom;
import br.com.fiap.bluerewards.model.Usuario;

public record UsuarioResponseCupom(Long id, String nome, String email, int pontuacao, Set<Cupom> cupons) {

    public static UsuarioResponseCupom fromUsuario(Usuario usuario) {
        return new UsuarioResponseCupom(usuario.getId(), usuario.getNome(), usuario.getEmail(), usuario.getPontuacao(), usuario.getCupons());
    }
    
}
