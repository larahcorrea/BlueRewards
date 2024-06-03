package br.com.fiap.bluerewards.dto;

import br.com.fiap.bluerewards.model.Usuario;

public record UsuarioResponse(Long id, String nome, String email, int pontuacao) {

    public static UsuarioResponse fromUsuario(Usuario usuario) {
        return new UsuarioResponse(usuario.getId(), usuario.getNome(), usuario.getEmail(), usuario.getPontuacao());
    }
    
}
