package br.com.fiap.bluerewards.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import br.com.fiap.bluerewards.dto.Credenciais;
import br.com.fiap.bluerewards.dto.Token;
import br.com.fiap.bluerewards.dto.UsuarioResponse;
import br.com.fiap.bluerewards.model.Usuario;
import br.com.fiap.bluerewards.repository.UsuarioRepository;
import br.com.fiap.bluerewards.service.TokenService;
import jakarta.validation.Valid;
import lombok.extern.log4j.Log4j2;

@RestController
@Log4j2
public class AutenticacaoController {

    @Autowired
    UsuarioRepository repository;

    @Autowired
    TokenService service;

    @Autowired
    AuthenticationManager authManager;

    @Autowired
    PasswordEncoder encoder;

    @PostMapping("/login")
    public ResponseEntity<Token> login(@RequestBody Credenciais credenciais) {
        log.info(credenciais);
        authManager.authenticate(credenciais.toAuthentication());
        return ResponseEntity.ok(service.generateToken(credenciais.email()));
    }
    
    
    @PostMapping("/usuario")
    public ResponseEntity<UsuarioResponse> create(@RequestBody @Valid Usuario usuario){
        usuario.setSenha(encoder.encode(usuario.getSenha()));
        repository.save(usuario);
        return ResponseEntity.status(HttpStatus.CREATED).body(UsuarioResponse.fromUsuario(usuario));

    }
}
