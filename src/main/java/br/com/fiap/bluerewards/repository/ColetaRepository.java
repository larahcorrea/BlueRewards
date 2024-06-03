package br.com.fiap.bluerewards.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.fiap.bluerewards.model.Coleta;
import br.com.fiap.bluerewards.model.Usuario;

public interface ColetaRepository extends JpaRepository<Coleta, Long>{

    Optional<List<Coleta>> findByUsuario(Usuario usuario);
    
}
