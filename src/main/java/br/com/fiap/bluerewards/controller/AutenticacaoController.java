package br.com.fiap.bluerewards.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import br.com.fiap.bluerewards.model.Usuario;
import br.com.fiap.bluerewards.repository.UsuarioRepository;

@RestController
public class AutenticacaoController {

    @Autowired
    UsuarioRepository repository;
    
    @PostMapping("/usuario")
    public ResponseEntity<Usuario> create(@RequestBody Usuario usuario){
        repository.save(usuario);
        return ResponseEntity.status(HttpStatus.CREATED).body(usuario);

    }
}
