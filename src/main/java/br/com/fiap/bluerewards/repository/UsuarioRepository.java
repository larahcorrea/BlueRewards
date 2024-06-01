package br.com.fiap.bluerewards.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.fiap.bluerewards.model.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Long>{
    
}
