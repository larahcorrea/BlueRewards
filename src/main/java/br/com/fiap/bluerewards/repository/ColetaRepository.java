package br.com.fiap.bluerewards.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.fiap.bluerewards.model.Coleta;

public interface ColetaRepository extends JpaRepository<Coleta, Long>{
    
}
